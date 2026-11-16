package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.SubtipoVeiculo;
import com.fleetops.motorista.domain.Veiculo;
import com.fleetops.motorista.dto.ranking.DetalheMotoristaManifestosResponseDTO;
import com.fleetops.motorista.dto.ranking.GraficoDestinoComparativoDTO;
import com.fleetops.motorista.dto.ranking.GraficoFaixaItemDTO;
import com.fleetops.motorista.dto.ranking.GraficoSubcategoriaItemDTO;
import com.fleetops.motorista.dto.ranking.ManifestoItemDetalheDTO;
import com.fleetops.motorista.dto.ranking.MotoristaNegativoItemDTO;
import com.fleetops.motorista.dto.ranking.PaginaNegativosResponseDTO;
import com.fleetops.motorista.dto.ranking.RankingMotoristaItemDTO;
import com.fleetops.motorista.dto.ranking.RankingRentabilidadeResponseDTO;
import com.fleetops.motorista.dto.ranking.ResumoNegativosDTO;
import com.fleetops.motorista.dto.ranking.ResumoRankingDTO;
import com.fleetops.motorista.dto.ranking.VisaoRanking;
import com.fleetops.motorista.exceptions.MotoristaNaoEncontradoException;
import com.fleetops.motorista.repository.ManifestoRepositorio;
import com.fleetops.motorista.util.DestinoGrandeSpUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentabilidadeService {

    private final ManifestoRepositorio manifestoRepositorio;

    @Transactional(readOnly = true)
    public List<String> listarMesesDisponiveis() {
        List<LocalDate> datas = manifestoRepositorio.listarDatasManifestosOrdenadasDesc();
        return datas.stream()
                .map(YearMonth::from)
                .distinct()
                .map(YearMonth::toString)
                .toList();
    }

    @Transactional(readOnly = true)
    public RankingRentabilidadeResponseDTO obterRankingModal(String mesParam, String visaoParam) {
        List<String> mesesDisponiveis = listarMesesDisponiveis();
        VisaoRanking visao = VisaoRanking.fromParam(visaoParam);
        YearMonth mesSelecionado = resolverMesSelecionado(mesParam, mesesDisponiveis);
        YearMonth mesAnterior = mesSelecionado.minusMonths(1);

        List<Manifesto> manifestosMesGeral = manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                mesSelecionado.atDay(1), mesSelecionado.atEndOfMonth());
        List<Manifesto> manifestosMesAnteriorGeral = manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                mesAnterior.atDay(1), mesAnterior.atEndOfMonth());

        boolean possuiManifestosImportadosNoMes = !manifestosMesGeral.isEmpty();
        boolean possuiDadosMesAnteriorSistema = !manifestosMesAnteriorGeral.isEmpty();

        List<Manifesto> manifestosMesVisao = filtrarPorVisao(manifestosMesGeral, visao);
        List<Manifesto> manifestosMesAnteriorVisao = filtrarPorVisao(manifestosMesAnteriorGeral, visao);

        boolean possuiDadosMes = !manifestosMesVisao.isEmpty();

        Map<Long, AgregadoMotoristaInterno> mapaMesAtual = agruparPorMotorista(manifestosMesVisao);
        Map<Long, AgregadoMotoristaInterno> mapaMesAnterior = agruparPorMotorista(manifestosMesAnteriorVisao);

        BigDecimal mediaFrotaMes = calcularMediaFrota(manifestosMesVisao);
        BigDecimal mediaFrotaMesAnterior =
                possuiDadosMesAnteriorSistema ? calcularMediaFrota(manifestosMesAnteriorVisao) : null;
        BigDecimal variacaoFrotaPercentual = calcularVariacaoPercentual(mediaFrotaMes, mediaFrotaMesAnterior);

        List<AgregadoMotoristaInterno> ordenados = new ArrayList<>(mapaMesAtual.values());
        ordenados.sort(comparadorOficialRanking());

        int totalMotoristas = ordenados.size();
        int totalMotoristasNegativos = 0;
        List<RankingMotoristaItemDTO> itens = new ArrayList<>(totalMotoristas);

        for (int i = 0; i < totalMotoristas; i++) {
            AgregadoMotoristaInterno ag = ordenados.get(i);
            int posicao = i + 1;

            BigDecimal variacaoMotorista = null;
            String statusVariacao;
            if (!possuiDadosMesAnteriorSistema) {
                statusVariacao = "SEM_MES_ANTERIOR";
            } else {
                AgregadoMotoristaInterno ant = mapaMesAnterior.get(ag.motorista.getIdMotorista());
                if (ant == null) {
                    statusVariacao = "NOVO";
                } else {
                    variacaoMotorista = ag.rentabilidadeMedia
                            .subtract(ant.rentabilidadeMedia)
                            .setScale(2, RoundingMode.HALF_UP);
                    int cmp = variacaoMotorista.compareTo(BigDecimal.ZERO);
                    statusVariacao = cmp > 0 ? "AUMENTO" : (cmp < 0 ? "QUEDA" : "IGUAL");
                }
            }

            boolean top3 = posicao <= 3;
            boolean ultimos3 = totalMotoristas > 3 && posicao > Math.max(3, totalMotoristas - 3);
            boolean rentabilidadeNegativa = ag.rentabilidadeMedia.compareTo(BigDecimal.ZERO) < 0;
            if (rentabilidadeNegativa) {
                totalMotoristasNegativos++;
            }
            boolean abaixoMedia = mediaFrotaMes != null && ag.rentabilidadeMedia.compareTo(mediaFrotaMes) < 0;

            String corDestaque = definirCorDestaque(top3, ultimos3, rentabilidadeNegativa, abaixoMedia);

            itens.add(new RankingMotoristaItemDTO(
                    posicao,
                    ag.motorista.getIdMotorista(),
                    ag.motorista.getNomeMotorista(),
                    ag.motorista.getCpfMotorista(),
                    ag.tipoVeiculo,
                    ag.subtipoVeiculo,
                    ag.subtipoLabel,
                    ag.numeroManifestos,
                    ag.rentabilidadeMedia,
                    variacaoMotorista,
                    statusVariacao,
                    ag.margemPercentual,
                    ag.rentabilidadeTotal,
                    top3,
                    ultimos3,
                    abaixoMedia,
                    rentabilidadeNegativa,
                    corDestaque));
        }

        ResumoRankingDTO resumo = new ResumoRankingDTO(
                mesSelecionado.toString(),
                mesAnterior.toString(),
                visao,
                mediaFrotaMes,
                mediaFrotaMesAnterior,
                variacaoFrotaPercentual,
                totalMotoristas,
                totalMotoristasNegativos,
                manifestosMesVisao.size(),
                possuiDadosMes,
                possuiManifestosImportadosNoMes,
                possuiDadosMesAnteriorSistema);

        List<GraficoFaixaItemDTO> graficoFaixas = construirGraficoFaixas(ordenados, mediaFrotaMes);
        List<GraficoSubcategoriaItemDTO> graficoSubcategorias =
                construirGraficoSubcategorias(manifestosMesVisao, mediaFrotaMes);
        GraficoDestinoComparativoDTO graficoDestino = construirGraficoGrandeSpVsDemais(manifestosMesGeral);

        return new RankingRentabilidadeResponseDTO(
                resumo, itens, graficoFaixas, graficoSubcategorias, graficoDestino, mesesDisponiveis);
    }

    public YearMonth resolverMesSelecionado(String mesParam, List<String> mesesDisponiveis) {
        if (mesParam != null && !mesParam.isBlank()) {
            try {
                return YearMonth.parse(mesParam.trim());
            } catch (DateTimeParseException ignored) {
                // fallback para mês mais recente
            }
        }
        if (mesesDisponiveis != null && !mesesDisponiveis.isEmpty()) {
            return YearMonth.parse(mesesDisponiveis.get(0));
        }
        return YearMonth.now();
    }

    public List<Manifesto> filtrarPorVisao(List<Manifesto> manifestos, VisaoRanking visao) {
        if (visao == VisaoRanking.GRANDE_SP) {
            return manifestos.stream()
                    .filter(m -> DestinoGrandeSpUtil.isGrandeSp(m.getDestino()))
                    .toList();
        }
        return manifestos;
    }

    public static BigDecimal calcularRtManifesto(Manifesto m) {
        BigDecimal vf = m.getFreteEmpresa() != null ? m.getFreteEmpresa() : BigDecimal.ZERO;
        BigDecimal co = m.getFreteAgregado() != null ? m.getFreteAgregado() : BigDecimal.ZERO;
        return vf.subtract(co).setScale(2, RoundingMode.HALF_UP);
    }

    private Map<Long, AgregadoMotoristaInterno> agruparPorMotorista(List<Manifesto> manifestos) {
        Map<Long, List<Manifesto>> porMotorista = new HashMap<>();
        for (Manifesto m : manifestos) {
            if (m.getMotorista() == null || m.getMotorista().getIdMotorista() == null) {
                continue;
            }
            porMotorista
                    .computeIfAbsent(m.getMotorista().getIdMotorista(), k -> new ArrayList<>())
                    .add(m);
        }

        Map<Long, AgregadoMotoristaInterno> resultado = new HashMap<>();
        for (Map.Entry<Long, List<Manifesto>> entry : porMotorista.entrySet()) {
            List<Manifesto> lista = entry.getValue();
            Motorista motorista = lista.get(0).getMotorista();
            int nm = lista.size();

            BigDecimal rtTotal = BigDecimal.ZERO;
            BigDecimal somaFreteEmpresa = BigDecimal.ZERO;
            LocalDate dataMaisRecente = LocalDate.MIN;
            long maiorIdManifesto = Long.MIN_VALUE;
            Veiculo veiculoRef = motorista.getVeiculo();

            for (Manifesto m : lista) {
                BigDecimal vf = m.getFreteEmpresa() != null ? m.getFreteEmpresa() : BigDecimal.ZERO;
                BigDecimal rt = calcularRtManifesto(m);
                rtTotal = rtTotal.add(rt);
                somaFreteEmpresa = somaFreteEmpresa.add(vf);

                LocalDate dataM = m.getDataManifesto() != null ? m.getDataManifesto() : LocalDate.MIN;
                long idM = m.getIdManifesto() != null ? m.getIdManifesto() : Long.MIN_VALUE;
                if (dataM.isAfter(dataMaisRecente) || (dataM.isEqual(dataMaisRecente) && idM > maiorIdManifesto)) {
                    dataMaisRecente = dataM;
                    maiorIdManifesto = idM;
                    if (veiculoRef == null && m.getVeiculo() != null) {
                        veiculoRef = m.getVeiculo();
                    }
                }
            }

            rtTotal = rtTotal.setScale(2, RoundingMode.HALF_UP);
            BigDecimal rm = rtTotal.divide(BigDecimal.valueOf(nm), 2, RoundingMode.HALF_UP);

            BigDecimal margem = null;
            if (somaFreteEmpresa.compareTo(BigDecimal.ZERO) > 0) {
                margem = rtTotal.multiply(BigDecimal.valueOf(100)).divide(somaFreteEmpresa, 1, RoundingMode.HALF_UP);
            }

            String tipoVeiculo = veiculoRef != null && veiculoRef.getTipoVeiculo() != null
                    ? veiculoRef.getTipoVeiculo().name()
                    : "NAO_INFORMADO";
            String subtipoVeiculo = veiculoRef != null && veiculoRef.getSubtipoVeiculo() != null
                    ? veiculoRef.getSubtipoVeiculo().name()
                    : "NAO_INFORMADO";
            String subtipoLabel = veiculoRef != null && veiculoRef.getSubtipoVeiculo() != null
                    ? formatarLabelSubtipo(veiculoRef.getSubtipoVeiculo())
                    : "Não informado";

            resultado.put(
                    entry.getKey(),
                    new AgregadoMotoristaInterno(
                            motorista,
                            tipoVeiculo,
                            subtipoVeiculo,
                            subtipoLabel,
                            nm,
                            rtTotal,
                            rm,
                            margem,
                            dataMaisRecente,
                            maiorIdManifesto));
        }

        return resultado;
    }

    public static String formatarLabelSubtipo(SubtipoVeiculo subtipo) {
        if (subtipo == null) {
            return "Não informado";
        }
        return switch (subtipo) {
            case VAN -> "VAN";
            case VUC -> "VUC";
            case TRES_QUARTOS -> "3/4";
            case FIORINO -> "FIORINO";
            case TOCO -> "TOCO";
            case TRUCK -> "TRUCK";
            case BI_TRUCK -> "BI-TRUCK";
            default -> subtipo.getDescricao();
        };
    }

    private Comparator<AgregadoMotoristaInterno> comparadorOficialRanking() {
        return (a, b) -> {
            int cmpRm = b.rentabilidadeMedia.compareTo(a.rentabilidadeMedia);
            if (cmpRm != 0) {
                return cmpRm;
            }
            int cmpNm = Integer.compare(b.numeroManifestos, a.numeroManifestos);
            if (cmpNm != 0) {
                return cmpNm;
            }
            int cmpData = b.dataManifestoMaisRecente.compareTo(a.dataManifestoMaisRecente);
            if (cmpData != 0) {
                return cmpData;
            }
            int cmpId = Long.compare(b.maiorIdManifesto, a.maiorIdManifesto);
            if (cmpId != 0) {
                return cmpId;
            }
            String nomeA = a.motorista.getNomeMotorista() != null ? a.motorista.getNomeMotorista() : "";
            String nomeB = b.motorista.getNomeMotorista() != null ? b.motorista.getNomeMotorista() : "";
            return nomeA.compareToIgnoreCase(nomeB);
        };
    }

    private BigDecimal calcularMediaFrota(List<Manifesto> manifestos) {
        if (manifestos == null || manifestos.isEmpty()) {
            return null;
        }
        BigDecimal somaRt = BigDecimal.ZERO;
        for (Manifesto m : manifestos) {
            somaRt = somaRt.add(calcularRtManifesto(m));
        }
        return somaRt.divide(BigDecimal.valueOf(manifestos.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularVariacaoPercentual(BigDecimal atual, BigDecimal anterior) {
        if (atual == null || anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return atual.subtract(anterior)
                .multiply(BigDecimal.valueOf(100))
                .divide(anterior.abs(), 1, RoundingMode.HALF_UP);
    }

    private String definirCorDestaque(
            boolean top3, boolean ultimos3, boolean rentabilidadeNegativa, boolean abaixoMedia) {
        if (ultimos3 || rentabilidadeNegativa) {
            return "VERMELHO";
        }
        if (top3) {
            return "VERDE";
        }
        if (abaixoMedia) {
            return "AMARELO";
        }
        return "NEUTRO";
    }

    private List<GraficoFaixaItemDTO> construirGraficoFaixas(
            List<AgregadoMotoristaInterno> motoristas, BigDecimal mediaFrotaMes) {
        String[] codigos = {
            "MENOR_QUE_0",
            "DE_0_A_500",
            "DE_500_A_1000",
            "DE_1000_A_1500",
            "DE_1500_A_2000",
            "DE_2000_A_2500",
            "DE_2500_A_3000",
            "MAIOR_OU_IGUAL_3000"
        };
        String[] rotulos = {
            "< 0", "0–500", "500–1.000", "1.000–1.500", "1.500–2.000", "2.000–2.500", "2.500–3.000", "> 3.000"
        };
        BigDecimal[] limitesInf = {
            null,
            BigDecimal.ZERO,
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(1500),
            BigDecimal.valueOf(2000),
            BigDecimal.valueOf(2500),
            BigDecimal.valueOf(3000)
        };
        BigDecimal[] limitesSup = {
            BigDecimal.ZERO,
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(1500),
            BigDecimal.valueOf(2000),
            BigDecimal.valueOf(2500),
            BigDecimal.valueOf(3000),
            null
        };

        int[] contagens = new int[8];
        for (AgregadoMotoristaInterno m : motoristas) {
            int idx = indiceFaixa(m.rentabilidadeMedia);
            contagens[idx]++;
        }

        int idxMedia = mediaFrotaMes != null ? indiceFaixa(mediaFrotaMes) : -1;

        List<GraficoFaixaItemDTO> faixas = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            String cor;
            if (i == 0) {
                cor = "VERMELHO";
            } else if (idxMedia == -1 || i >= idxMedia) {
                cor = "AZUL";
            } else {
                cor = "AMARELO";
            }
            faixas.add(new GraficoFaixaItemDTO(
                    codigos[i], rotulos[i], limitesInf[i], limitesSup[i], contagens[i], cor, i == idxMedia));
        }
        return faixas;
    }

    public static int indiceFaixa(BigDecimal rm) {
        if (rm.compareTo(BigDecimal.ZERO) < 0) {
            return 0;
        }
        if (rm.compareTo(BigDecimal.valueOf(500)) < 0) {
            return 1;
        }
        if (rm.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return 2;
        }
        if (rm.compareTo(BigDecimal.valueOf(1500)) < 0) {
            return 3;
        }
        if (rm.compareTo(BigDecimal.valueOf(2000)) < 0) {
            return 4;
        }
        if (rm.compareTo(BigDecimal.valueOf(2500)) < 0) {
            return 5;
        }
        if (rm.compareTo(BigDecimal.valueOf(3000)) < 0) {
            return 6;
        }
        return 7;
    }

    private List<GraficoSubcategoriaItemDTO> construirGraficoSubcategorias(
            List<Manifesto> manifestosVisao, BigDecimal mediaFrotaMes) {
        if (manifestosVisao.isEmpty()) {
            return List.of();
        }

        Map<SubtipoVeiculo, List<Manifesto>> porSubtipo = new EnumMap<>(SubtipoVeiculo.class);
        for (Manifesto m : manifestosVisao) {
            SubtipoVeiculo subtipo = m.getVeiculo() != null ? m.getVeiculo().getSubtipoVeiculo() : null;
            if (subtipo == null && m.getMotorista() != null && m.getMotorista().getVeiculo() != null) {
                subtipo = m.getMotorista().getVeiculo().getSubtipoVeiculo();
            }
            if (subtipo != null) {
                porSubtipo.computeIfAbsent(subtipo, k -> new ArrayList<>()).add(m);
            }
        }

        List<GraficoSubcategoriaItemDTO> lista = new ArrayList<>();
        for (Map.Entry<SubtipoVeiculo, List<Manifesto>> entry : porSubtipo.entrySet()) {
            SubtipoVeiculo subtipo = entry.getKey();
            List<Manifesto> ms = entry.getValue();
            int qtdManifestos = ms.size();
            Set<Long> motoristasUnicos = new HashSet<>();
            BigDecimal somaRt = BigDecimal.ZERO;

            for (Manifesto m : ms) {
                if (m.getMotorista() != null && m.getMotorista().getIdMotorista() != null) {
                    motoristasUnicos.add(m.getMotorista().getIdMotorista());
                }
                somaRt = somaRt.add(calcularRtManifesto(m));
            }

            BigDecimal rmSub = somaRt.divide(BigDecimal.valueOf(qtdManifestos), 2, RoundingMode.HALF_UP);
            String cor = (mediaFrotaMes != null && rmSub.compareTo(mediaFrotaMes) < 0) ? "AMARELO" : "AZUL";

            lista.add(new GraficoSubcategoriaItemDTO(
                    subtipo.name(), formatarLabelSubtipo(subtipo), motoristasUnicos.size(), qtdManifestos, rmSub, cor));
        }

        lista.sort((a, b) -> {
            int cmp = Integer.compare(b.quantidadeManifestos(), a.quantidadeManifestos());
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compare(
                    SubtipoVeiculo.valueOf(a.subtipoVeiculo()).ordinal(),
                    SubtipoVeiculo.valueOf(b.subtipoVeiculo()).ordinal());
        });

        return lista.stream().limit(4).toList();
    }

    private GraficoDestinoComparativoDTO construirGraficoGrandeSpVsDemais(List<Manifesto> manifestosMesGeral) {
        BigDecimal mediaGeral = calcularMediaFrota(manifestosMesGeral);

        List<Manifesto> grandeSp = new ArrayList<>();
        List<Manifesto> demais = new ArrayList<>();
        for (Manifesto m : manifestosMesGeral) {
            if (DestinoGrandeSpUtil.isGrandeSp(m.getDestino())) {
                grandeSp.add(m);
            } else {
                demais.add(m);
            }
        }

        BigDecimal rmGrandeSp = calcularMediaFrota(grandeSp);
        BigDecimal rmDemais = calcularMediaFrota(demais);

        String corGrandeSp =
                (rmGrandeSp != null && mediaGeral != null && rmGrandeSp.compareTo(mediaGeral) < 0) ? "AMARELO" : "AZUL";
        String corDemais =
                (rmDemais != null && mediaGeral != null && rmDemais.compareTo(mediaGeral) < 0) ? "AMARELO" : "AZUL";

        return new GraficoDestinoComparativoDTO(
                mediaGeral, rmGrandeSp, grandeSp.size(), corGrandeSp, rmDemais, demais.size(), corDemais);
    }

    @Transactional(readOnly = true)
    public PaginaNegativosResponseDTO obterPaginaNegativos(String mesParam, String visaoParam) {
        List<String> mesesDisponiveis = listarMesesDisponiveis();
        VisaoRanking visao = VisaoRanking.fromParam(visaoParam);
        YearMonth mesSelecionado = resolverMesSelecionado(mesParam, mesesDisponiveis);
        YearMonth mesAnterior = mesSelecionado.minusMonths(1);

        List<Manifesto> manifestosMesGeral = manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                mesSelecionado.atDay(1), mesSelecionado.atEndOfMonth());
        List<Manifesto> manifestosMesAnteriorGeral = manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                mesAnterior.atDay(1), mesAnterior.atEndOfMonth());

        boolean possuiDadosMesAnteriorSistema = !manifestosMesAnteriorGeral.isEmpty();

        List<Manifesto> manifestosMesVisao = filtrarPorVisao(manifestosMesGeral, visao);
        List<Manifesto> manifestosMesAnteriorVisao = filtrarPorVisao(manifestosMesAnteriorGeral, visao);

        boolean possuiDadosMes = !manifestosMesVisao.isEmpty();

        Map<Long, AgregadoMotoristaInterno> mapaMesAtual = agruparPorMotorista(manifestosMesVisao);
        Map<Long, AgregadoMotoristaInterno> mapaMesAnterior = agruparPorMotorista(manifestosMesAnteriorVisao);

        // Agrupamento de manifestos por motorista para contar manifestos com RT negativa
        Map<Long, Integer> manifestosNegativosPorMotorista = new HashMap<>();
        for (Manifesto m : manifestosMesVisao) {
            if (m.getMotorista() != null && m.getMotorista().getIdMotorista() != null) {
                BigDecimal rt = calcularRtManifesto(m);
                if (rt.compareTo(BigDecimal.ZERO) < 0) {
                    manifestosNegativosPorMotorista.merge(m.getMotorista().getIdMotorista(), 1, Integer::sum);
                }
            }
        }

        List<AgregadoMotoristaInterno> ordenadosGeral = new ArrayList<>(mapaMesAtual.values());
        ordenadosGeral.sort(comparadorOficialRanking());

        int totalMotoristasFrota = ordenadosGeral.size();
        List<MotoristaNegativoItemDTO> motoristasNegativos = new ArrayList<>();
        BigDecimal rtTotalNegativos = BigDecimal.ZERO;

        for (int i = 0; i < totalMotoristasFrota; i++) {
            AgregadoMotoristaInterno ag = ordenadosGeral.get(i);
            int posicaoOficial = i + 1;

            if (ag.rentabilidadeMedia.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal variacaoMotorista = null;
                String statusVariacao;
                if (!possuiDadosMesAnteriorSistema) {
                    statusVariacao = "SEM_MES_ANTERIOR";
                } else {
                    AgregadoMotoristaInterno ant = mapaMesAnterior.get(ag.motorista.getIdMotorista());
                    if (ant == null) {
                        statusVariacao = "NOVO";
                    } else {
                        variacaoMotorista = ag.rentabilidadeMedia
                                .subtract(ant.rentabilidadeMedia)
                                .setScale(2, RoundingMode.HALF_UP);
                        int cmp = variacaoMotorista.compareTo(BigDecimal.ZERO);
                        statusVariacao = cmp > 0 ? "AUMENTO" : (cmp < 0 ? "QUEDA" : "IGUAL");
                    }
                }

                int qtdNeg = manifestosNegativosPorMotorista.getOrDefault(ag.motorista.getIdMotorista(), 0);
                String fracaoNegativos = qtdNeg + " de " + ag.numeroManifestos;
                String posicaoFormatada = posicaoOficial + "º";

                rtTotalNegativos = rtTotalNegativos.add(ag.rentabilidadeTotal);

                motoristasNegativos.add(new MotoristaNegativoItemDTO(
                        posicaoOficial,
                        posicaoFormatada,
                        ag.motorista.getIdMotorista(),
                        ag.motorista.getNomeMotorista(),
                        ag.motorista.getCpfMotorista(),
                        ag.tipoVeiculo,
                        ag.subtipoVeiculo,
                        ag.subtipoLabel,
                        ag.rentabilidadeMedia,
                        variacaoMotorista,
                        statusVariacao,
                        ag.numeroManifestos,
                        qtdNeg,
                        fracaoNegativos,
                        ag.rentabilidadeTotal));
            }
        }

        // Ordenação da tabela da US.15: Pior RM primeiro (mais negativo ao menos negativo)
        motoristasNegativos.sort((a, b) -> {
            int cmpRm = a.rentabilidadeMedia().compareTo(b.rentabilidadeMedia());
            if (cmpRm != 0) {
                return cmpRm; // Menor (mais negativo) vem primeiro
            }
            return Integer.compare(b.posicao(), a.posicao());
        });

        int qtdNegativos = motoristasNegativos.size();
        BigDecimal percentualNegativos = BigDecimal.ZERO;
        if (totalMotoristasFrota > 0) {
            percentualNegativos = BigDecimal.valueOf(qtdNegativos)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalMotoristasFrota), 1, RoundingMode.HALF_UP);
        }

        ResumoNegativosDTO resumo = new ResumoNegativosDTO(
                mesSelecionado.toString(),
                mesAnterior.toString(),
                visao,
                qtdNegativos,
                totalMotoristasFrota,
                percentualNegativos,
                rtTotalNegativos.setScale(2, RoundingMode.HALF_UP),
                possuiDadosMes);

        return new PaginaNegativosResponseDTO(resumo, motoristasNegativos, mesesDisponiveis);
    }

    @Transactional(readOnly = true)
    public DetalheMotoristaManifestosResponseDTO obterManifestosMotoristaNegativo(
            Long motoristaId, String mesParam, String visaoParam) {
        List<String> mesesDisponiveis = listarMesesDisponiveis();
        VisaoRanking visao = VisaoRanking.fromParam(visaoParam);
        YearMonth mesSelecionado = resolverMesSelecionado(mesParam, mesesDisponiveis);

        List<Manifesto> manifestosMesGeral = manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                mesSelecionado.atDay(1), mesSelecionado.atEndOfMonth());
        List<Manifesto> manifestosMesVisao = filtrarPorVisao(manifestosMesGeral, visao);

        Map<Long, AgregadoMotoristaInterno> mapaMesAtual = agruparPorMotorista(manifestosMesVisao);
        AgregadoMotoristaInterno agregadoMotorista = mapaMesAtual.get(motoristaId);

        if (agregadoMotorista == null) {
            throw new MotoristaNaoEncontradoException(motoristaId);
        }

        List<AgregadoMotoristaInterno> ordenadosGeral = new ArrayList<>(mapaMesAtual.values());
        ordenadosGeral.sort(comparadorOficialRanking());

        int posicaoOficial = 0;
        for (int i = 0; i < ordenadosGeral.size(); i++) {
            if (ordenadosGeral.get(i).motorista.getIdMotorista().equals(motoristaId)) {
                posicaoOficial = i + 1;
                break;
            }
        }

        List<Manifesto> manifestosDoMotorista = manifestosMesVisao.stream()
                .filter(m -> m.getMotorista() != null
                        && motoristaId.equals(m.getMotorista().getIdMotorista()))
                .toList();

        List<ManifestoItemDetalheDTO> itensManifestos = new ArrayList<>();
        int manifestosNegativosCount = 0;

        for (Manifesto m : manifestosDoMotorista) {
            BigDecimal vf = m.getFreteEmpresa() != null ? m.getFreteEmpresa() : BigDecimal.ZERO;
            BigDecimal co = m.getFreteAgregado() != null ? m.getFreteAgregado() : BigDecimal.ZERO;
            BigDecimal rt = vf.subtract(co).setScale(2, RoundingMode.HALF_UP);

            if (rt.compareTo(BigDecimal.ZERO) < 0) {
                manifestosNegativosCount++;
            }

            String dataFormatada =
                    m.getDataManifesto() != null ? m.getDataManifesto().toString() : "—";
            String numeroManifesto = m.getIdManifesto() != null ? String.valueOf(m.getIdManifesto()) : "—";

            itensManifestos.add(new ManifestoItemDetalheDTO(
                    m.getIdManifesto(),
                    numeroManifesto,
                    dataFormatada,
                    m.getDestino() != null ? m.getDestino() : "Não informado",
                    vf.setScale(2, RoundingMode.HALF_UP),
                    co.setScale(2, RoundingMode.HALF_UP),
                    rt));
        }

        // Ordenar manifestos pelo pior RT primeiro (menor valor/mais negativo ao maior)
        itensManifestos.sort((a, b) -> {
            int cmp = a.rtManifesto().compareTo(b.rtManifesto());
            if (cmp != 0) {
                return cmp;
            }
            return Long.compare(
                    b.idManifesto() != null ? b.idManifesto() : 0L, a.idManifesto() != null ? a.idManifesto() : 0L);
        });

        String fracaoNegativos = manifestosNegativosCount + " de " + agregadoMotorista.numeroManifestos;
        String posicaoFormatada = posicaoOficial > 0 ? posicaoOficial + "º" : "—";

        return new DetalheMotoristaManifestosResponseDTO(
                motoristaId,
                agregadoMotorista.motorista.getNomeMotorista(),
                agregadoMotorista.subtipoLabel,
                mesSelecionado.toString(),
                visao,
                posicaoOficial,
                posicaoFormatada,
                agregadoMotorista.rentabilidadeMedia,
                agregadoMotorista.rentabilidadeTotal,
                agregadoMotorista.numeroManifestos,
                manifestosNegativosCount,
                fracaoNegativos,
                itensManifestos);
    }

    private record AgregadoMotoristaInterno(
            Motorista motorista,
            String tipoVeiculo,
            String subtipoVeiculo,
            String subtipoLabel,
            int numeroManifestos,
            BigDecimal rentabilidadeTotal,
            BigDecimal rentabilidadeMedia,
            BigDecimal margemPercentual,
            LocalDate dataManifestoMaisRecente,
            long maiorIdManifesto) {}
}

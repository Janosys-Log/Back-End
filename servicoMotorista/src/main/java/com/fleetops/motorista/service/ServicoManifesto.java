package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.RendimentoMensal;
import com.fleetops.motorista.domain.Veiculo;
import com.fleetops.motorista.dto.DashboardOperacionalDTO;
import com.fleetops.motorista.dto.ManifestoDestaqueDTO;
import com.fleetops.motorista.dto.RentabilidadeDTO;
import com.fleetops.motorista.dto.ResponseDate;
import com.fleetops.motorista.ouvinte.dto.response.ManifestoDTO;
import com.fleetops.motorista.repository.ManifestoRepositorio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ServicoManifesto {

    @Autowired
    private ManifestoRepositorio manifestoRepo;

    @Autowired
    private ServicoAgregado servAgregado;

    @Autowired
    private ServicoMotorista servicoMotorista;

    @Autowired
    private ServicoVeiculo sservicoVeiculo;

    public List<Manifesto> listarTodos() {
        return manifestoRepo.findAll();
    }

    public Optional<Manifesto> buscarPorId(Long id) {
        return manifestoRepo.findById(id);
    }

    public Optional<ResponseDate> buscarDataUltimoManifesto() {
        return manifestoRepo
                .findFirstByOrderByDataInsertDesc()
                .map(manifesto -> new ResponseDate(manifesto.getDataInsert()));
    }

    @Transactional
    public int salvarManifestosEmLote(List<ManifestoDTO> manifestos) {
        if (manifestos == null || manifestos.isEmpty()) {
            return 0;
        }

        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

        // 1. Identificar quais IDs já existem no banco de uma só vez (chunks de 500)
        Set<Long> idsExistentes = new HashSet<>();
        List<Long> todosIds = manifestos.stream()
                .map(ManifestoDTO::getManifestoid)
                .filter(Objects::nonNull)
                .toList();

        int chunkSize = 500;
        for (int i = 0; i < todosIds.size(); i += chunkSize) {
            List<Long> subIds = todosIds.subList(i, Math.min(i + chunkSize, todosIds.size()));
            if (!subIds.isEmpty()) {
                idsExistentes.addAll(manifestoRepo.findExistingIds(subIds));
            }
        }

        // 2. Caches locais em memória para evitar ir ao banco a cada linha repetida
        Map<String, Agregado> cacheAgregadoPorCnpj = new HashMap<>();
        Map<String, Agregado> cacheAgregadoPorNome = new HashMap<>();
        Map<String, Veiculo> cacheVeiculoPorPlaca = new HashMap<>();
        Map<String, Motorista> cacheMotoristaPorCpf = new HashMap<>();
        Map<String, Motorista> cacheMotoristaPorNome = new HashMap<>();

        List<Manifesto> paraSalvar = new ArrayList<>();
        Set<Long> idsJaProcessadosNoLote = new HashSet<>();

        for (ManifestoDTO dto : manifestos) {
            if (dto.getManifestoid() == null) continue;
            if (idsExistentes.contains(dto.getManifestoid())) continue;
            if (!idsJaProcessadosNoLote.add(dto.getManifestoid())) continue;

            try {
                Manifesto manifesto = new Manifesto();
                manifesto.setIdManifesto(dto.getManifestoid());

                if (dto.getData() != null && !dto.getData().isBlank()) {
                    manifesto.setDataManifesto(LocalDate.parse(dto.getData(), FMT));
                }
                manifesto.setDestino(dto.getDestino());

                BigDecimal freteEmpresa =
                        dto.getValorfreteempresa() != null ? dto.getValorfreteempresa() : BigDecimal.ZERO;
                BigDecimal freteAgregado =
                        dto.getValorfreteagregado() != null ? dto.getValorfreteagregado() : BigDecimal.ZERO;
                manifesto.setFreteAgregado(freteAgregado);
                manifesto.setFreteEmpresa(freteEmpresa);
                manifesto.setValorRecebido(freteEmpresa.subtract(freteAgregado));
                manifesto.setObservacoes(dto.getObservacoes());

                // Resolve Agregado usando cache em memória
                Agregado agregado = null;
                String cnpj = dto.getCnpjagregado();
                String nomeAgregado = dto.getAgregado();

                if (cnpj != null && !cnpj.isBlank()) {
                    agregado = cacheAgregadoPorCnpj.get(cnpj);
                    if (agregado == null) {
                        agregado = servAgregado.pegarPorCnpj(cnpj);
                    }
                }
                if (agregado == null && nomeAgregado != null && !nomeAgregado.isBlank()) {
                    agregado = cacheAgregadoPorNome.get(nomeAgregado);
                    if (agregado == null) {
                        agregado = servAgregado.pegarPorNome(nomeAgregado);
                    }
                }
                if (agregado == null
                        && ((nomeAgregado != null && !nomeAgregado.isBlank()) || (cnpj != null && !cnpj.isBlank()))) {
                    agregado = servAgregado.criarComDadosManifesto(nomeAgregado, cnpj);
                }
                if (agregado != null) {
                    if (cnpj != null && !cnpj.isBlank()) cacheAgregadoPorCnpj.put(cnpj, agregado);
                    if (nomeAgregado != null && !nomeAgregado.isBlank())
                        cacheAgregadoPorNome.put(nomeAgregado, agregado);
                    if (agregado.getNomeAgregado() != null)
                        cacheAgregadoPorNome.put(agregado.getNomeAgregado(), agregado);
                }
                manifesto.setAgregado(agregado);

                // Resolve Veículo usando cache em memória
                Veiculo veiculo = null;
                String placa = dto.getPlaca();
                if (placa != null && !placa.isBlank()) {
                    veiculo = cacheVeiculoPorPlaca.get(placa);
                    if (veiculo == null) {
                        veiculo = sservicoVeiculo.acharPorPlaca(placa);
                    }
                    if (veiculo == null) {
                        veiculo = sservicoVeiculo.criarComPlacaEAgregado(placa, agregado);
                    }
                    if (veiculo != null) {
                        cacheVeiculoPorPlaca.put(placa, veiculo);
                    }
                }
                manifesto.setVeiculo(veiculo);

                // Resolve Motorista usando cache em memória
                Motorista motorista = null;
                String cpf = dto.getCpfmotorista();
                String nomeMotorista = dto.getMotorista();

                if (cpf != null && !cpf.isBlank()) {
                    motorista = cacheMotoristaPorCpf.get(cpf);
                    if (motorista == null) {
                        motorista = servicoMotorista.pegarPorCPF(cpf);
                    }
                }
                if (motorista == null && nomeMotorista != null && !nomeMotorista.isBlank()) {
                    motorista = cacheMotoristaPorNome.get(nomeMotorista);
                    if (motorista == null) {
                        motorista = servicoMotorista.pegarPorNome(nomeMotorista);
                    }
                }
                if (motorista == null
                        && ((nomeMotorista != null && !nomeMotorista.isBlank()) || (cpf != null && !cpf.isBlank()))) {
                    motorista = servicoMotorista.criarComDadosManifesto(nomeMotorista, cpf, veiculo, agregado);
                } else if (motorista != null && motorista.getVeiculo() == null && veiculo != null) {
                    motorista.setVeiculo(veiculo);
                    servicoMotorista.salvar(motorista);
                }
                if (motorista != null) {
                    if (cpf != null && !cpf.isBlank()) cacheMotoristaPorCpf.put(cpf, motorista);
                    if (nomeMotorista != null && !nomeMotorista.isBlank())
                        cacheMotoristaPorNome.put(nomeMotorista, motorista);
                    if (motorista.getNomeMotorista() != null)
                        cacheMotoristaPorNome.put(motorista.getNomeMotorista(), motorista);
                }
                manifesto.setMotorista(motorista);

                paraSalvar.add(manifesto);

            } catch (Exception e) {
                System.err.println("Erro ao preparar manifesto " + dto.getManifestoid() + ": " + e.getMessage());
            }
        }

        if (!paraSalvar.isEmpty()) {
            manifestoRepo.saveAll(paraSalvar);
        }

        return paraSalvar.size();
    }

    public void SalvaOuNaoNoBancoComAsInformacoesDoManifesto(ManifestoDTO manifesto) {
        if (manifestoRepo.existsById(manifesto.getManifestoid())) {
            return;
        }

        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

        Manifesto manifestoParaSalvar = new Manifesto();
        manifestoParaSalvar.setIdManifesto(manifesto.getManifestoid());
        manifestoParaSalvar.setDataManifesto(LocalDate.parse(manifesto.getData(), FMT));
        manifestoParaSalvar.setDestino(manifesto.getDestino());

        BigDecimal freteEmpresa =
                manifesto.getValorfreteempresa() != null ? manifesto.getValorfreteempresa() : BigDecimal.ZERO;
        BigDecimal freteAgregado =
                manifesto.getValorfreteagregado() != null ? manifesto.getValorfreteagregado() : BigDecimal.ZERO;
        manifestoParaSalvar.setFreteAgregado(freteAgregado);
        manifestoParaSalvar.setFreteEmpresa(freteEmpresa);
        manifestoParaSalvar.setValorRecebido(freteEmpresa.subtract(freteAgregado));
        manifestoParaSalvar.setObservacoes(manifesto.getObservacoes());

        Agregado agregado = null;
        if (manifesto.getCnpjagregado() != null && !manifesto.getCnpjagregado().isBlank()) {
            agregado = servAgregado.pegarPorCnpj(manifesto.getCnpjagregado());
        }
        if (agregado == null
                && manifesto.getAgregado() != null
                && !manifesto.getAgregado().isBlank()) {
            agregado = servAgregado.pegarPorNome(manifesto.getAgregado());
        }
        if (agregado == null) {
            agregado = servAgregado.criarComDadosManifesto(manifesto.getAgregado(), manifesto.getCnpjagregado());
        }
        manifestoParaSalvar.setAgregado(agregado);

        Veiculo veiculo = sservicoVeiculo.acharPorPlaca(manifesto.getPlaca());
        if (veiculo == null) {
            veiculo = sservicoVeiculo.criarComPlacaEAgregado(manifesto.getPlaca(), agregado);
        }
        manifestoParaSalvar.setVeiculo(veiculo);

        Motorista motorista = null;
        if (manifesto.getCpfmotorista() != null && !manifesto.getCpfmotorista().isBlank()) {
            motorista = servicoMotorista.pegarPorCPF(manifesto.getCpfmotorista());
        }
        if (motorista == null
                && manifesto.getMotorista() != null
                && !manifesto.getMotorista().isBlank()) {
            motorista = servicoMotorista.pegarPorNome(manifesto.getMotorista());
        }
        if (motorista == null) {
            motorista = servicoMotorista.criarComDadosManifesto(
                    manifesto.getMotorista(), manifesto.getCpfmotorista(), veiculo, agregado);
        } else if (motorista.getVeiculo() == null && veiculo != null) {
            motorista.setVeiculo(veiculo);
            servicoMotorista.salvar(motorista);
        }
        manifestoParaSalvar.setMotorista(motorista);

        manifestoRepo.save(manifestoParaSalvar);
    }

    public RentabilidadeDTO RentabilidademanifestosPorMotoristaEAnoEMes(Long id, Long mes, Long ano) {
        List<Manifesto> listaMnifestos = manifestoRepo.buscarPorMotoristaEMes(id, mes, ano);
        YearMonth mesAnterior = YearMonth.of(ano.intValue(), mes.intValue()).minusMonths(1);
        List<Manifesto> listaManifestoMesPassado = manifestoRepo.buscarPorMotoristaEMes(
                id, (long) mesAnterior.getMonthValue(), (long) mesAnterior.getYear());

        BigDecimal rentabilidadeMedia = listaMnifestos.stream()
                .map(m -> m.getValorRecebido())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(listaMnifestos.size(), 1)), 2, RoundingMode.HALF_UP);

        BigDecimal rentabilidadeMediaMesPassado = listaManifestoMesPassado.stream()
                .map(m -> m.getValorRecebido())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(listaManifestoMesPassado.size(), 1)), 2, RoundingMode.HALF_UP);

        BigDecimal crescimento = null;
        if (rentabilidadeMediaMesPassado.compareTo(BigDecimal.ZERO) != 0) {
            crescimento = rentabilidadeMedia
                    .subtract(rentabilidadeMediaMesPassado)
                    .divide(rentabilidadeMediaMesPassado, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal rentabilidadeTotal =
                listaMnifestos.stream().map(m -> m.getValorRecebido()).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RentabilidadeDTO(
                mes, ano, rentabilidadeMedia, crescimento, (long) listaMnifestos.size(), rentabilidadeTotal);
    }

    public HashMap<Integer, RentabilidadeDTO> RentabilidadeNoUltimosMeses(LocalDate inicio, LocalDate fim) {
        HashMap<Integer, RentabilidadeDTO> map = new HashMap<>();
        BigDecimal anterior = BigDecimal.ZERO;
        List<RendimentoMensal> rendimentos = manifestoRepo.RendimentoMedioPorMeses(inicio, fim);

        for (RendimentoMensal rendimento : rendimentos) {
            BigDecimal crescimento = BigDecimal.ZERO;
            if (anterior.compareTo(BigDecimal.ZERO) != 0) {
                crescimento = rendimento
                        .rendimentoMedio()
                        .subtract(anterior)
                        .divide(anterior, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            RentabilidadeDTO rentabilidade = new RentabilidadeDTO(
                    (long) rendimento.mes(),
                    (long) rendimento.ano(),
                    rendimento.rendimentoMedio(),
                    crescimento,
                    (long) rendimento.numeroManifestos(),
                    rendimento.rendimentoTotal());
            map.put(rendimento.mes(), rentabilidade);
            anterior = rendimento.rendimentoMedio();
        }
        return map;
    }

    public RentabilidadeDTO RentabilidademanifestosPorMotoristaEAnoEMesGrandeSP(Long id, Long mes, Long ano) {
        List<Manifesto> listaMnifestos = manifestoRepo.buscarPorMotoristaEMesGrandeSP(id, mes, ano);
        YearMonth mesAnterior = YearMonth.of(ano.intValue(), mes.intValue()).minusMonths(1);
        List<Manifesto> listaManifestoMesPassado = manifestoRepo.buscarPorMotoristaEMesGrandeSP(
                id, (long) mesAnterior.getMonthValue(), (long) mesAnterior.getYear());

        BigDecimal rentabilidadeMedia = listaMnifestos.stream()
                .map(m -> m.getValorRecebido())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(listaMnifestos.size(), 1)), 2, RoundingMode.HALF_UP);

        BigDecimal rentabilidadeMediaMesPassado = listaManifestoMesPassado.stream()
                .map(m -> m.getValorRecebido())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(listaManifestoMesPassado.size(), 1)), 2, RoundingMode.HALF_UP);

        BigDecimal crescimento = null;
        if (rentabilidadeMediaMesPassado.compareTo(BigDecimal.ZERO) != 0) {
            crescimento = rentabilidadeMedia
                    .subtract(rentabilidadeMediaMesPassado)
                    .divide(rentabilidadeMediaMesPassado, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal rentabilidadeTotal =
                listaMnifestos.stream().map(m -> m.getValorRecebido()).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RentabilidadeDTO(
                mes, ano, rentabilidadeMedia, crescimento, (long) listaMnifestos.size(), rentabilidadeTotal);
    }

    public HashMap<Integer, RentabilidadeDTO> RentabilidadeNoUltimosMesesGrandeSp(LocalDate inicio, LocalDate fim) {
        HashMap<Integer, RentabilidadeDTO> map = new HashMap<>();
        BigDecimal anterior = BigDecimal.ZERO;
        List<RendimentoMensal> rendimentos = manifestoRepo.RendimentoMedioPorMesesGrandeSP(inicio, fim);

        for (RendimentoMensal rendimento : rendimentos) {
            BigDecimal crescimento = BigDecimal.ZERO;
            if (anterior.compareTo(BigDecimal.ZERO) != 0) {
                crescimento = rendimento
                        .rendimentoMedio()
                        .subtract(anterior)
                        .divide(anterior, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            RentabilidadeDTO rentabilidade = new RentabilidadeDTO(
                    (long) rendimento.mes(),
                    (long) rendimento.ano(),
                    rendimento.rendimentoMedio(),
                    crescimento,
                    (long) rendimento.numeroManifestos(),
                    rendimento.rendimentoTotal());
            map.put(rendimento.mes(), rentabilidade);
            anterior = rendimento.rendimentoMedio();
        }
        return map;
    }

    @Transactional(readOnly = true)
    public DashboardOperacionalDTO obterDashboardOperacional(String mesParam) {
        YearMonth mesSelecionado;
        if (mesParam != null && !mesParam.isBlank()) {
            try {
                mesSelecionado = YearMonth.parse(mesParam.trim());
            } catch (Exception e) {
                mesSelecionado = YearMonth.now();
            }
        } else {
            List<LocalDate> datas = manifestoRepo.listarDatasManifestosOrdenadasDesc();
            if (!datas.isEmpty()) {
                mesSelecionado = YearMonth.from(datas.get(0));
            } else {
                mesSelecionado = YearMonth.now();
            }
        }

        YearMonth mesAnterior = mesSelecionado.minusMonths(1);

        LocalDate inicioMes = mesSelecionado.atDay(1);
        LocalDate fimMes = mesSelecionado.atEndOfMonth();
        LocalDate inicioMesAnterior = mesAnterior.atDay(1);
        LocalDate fimMesAnterior = mesAnterior.atEndOfMonth();

        List<Manifesto> manifestos = manifestoRepo.buscarPorPeriodoComMotoristaEVeiculo(inicioMes, fimMes);
        List<Manifesto> manifestosAnterior =
                manifestoRepo.buscarPorPeriodoComMotoristaEVeiculo(inicioMesAnterior, fimMesAnterior);

        long totalManifestos = manifestos.size();
        BigDecimal custoOperacionalTotal = manifestos.stream()
                .map(m -> m.getFreteAgregado() != null ? m.getFreteAgregado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal custoMedioPorManifesto = totalManifestos > 0
                ? custoOperacionalTotal.divide(BigDecimal.valueOf(totalManifestos), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalManifestosAnterior = manifestosAnterior.size();
        BigDecimal custoOperacionalTotalMesAnterior = manifestosAnterior.stream()
                .map(m -> m.getFreteAgregado() != null ? m.getFreteAgregado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal custoMedioPorManifestoMesAnterior = totalManifestosAnterior > 0
                ? custoOperacionalTotalMesAnterior.divide(
                        BigDecimal.valueOf(totalManifestosAnterior), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal variacaoCustoTotalPercentual = null;
        if (custoOperacionalTotalMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            variacaoCustoTotalPercentual = custoOperacionalTotal
                    .subtract(custoOperacionalTotalMesAnterior)
                    .divide(custoOperacionalTotalMesAnterior, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
        }

        BigDecimal variacaoCustoMedioPercentual = null;
        if (custoMedioPorManifestoMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            variacaoCustoMedioPercentual = custoMedioPorManifesto
                    .subtract(custoMedioPorManifestoMesAnterior)
                    .divide(custoMedioPorManifestoMesAnterior, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
        }

        Manifesto melhor = manifestos.stream()
                .filter(m -> m.getValorRecebido() != null)
                .max(Comparator.comparing(Manifesto::getValorRecebido))
                .orElse(null);

        Manifesto pior = manifestos.stream()
                .filter(m -> m.getValorRecebido() != null)
                .min(Comparator.comparing(Manifesto::getValorRecebido))
                .orElse(null);

        ManifestoDestaqueDTO melhorDTO = melhor != null ? toDestaqueDTO(melhor) : null;
        ManifestoDestaqueDTO piorDTO = pior != null ? toDestaqueDTO(pior) : null;

        return new DashboardOperacionalDTO(
                mesSelecionado.toString(),
                mesAnterior.toString(),
                custoOperacionalTotal,
                custoOperacionalTotalMesAnterior,
                variacaoCustoTotalPercentual,
                custoMedioPorManifesto,
                custoMedioPorManifestoMesAnterior,
                variacaoCustoMedioPercentual,
                totalManifestos,
                melhorDTO,
                piorDTO);
    }

    private ManifestoDestaqueDTO toDestaqueDTO(Manifesto m) {
        String motoristaNome = m.getMotorista() != null ? m.getMotorista().getNomeMotorista() : "—";
        String veiculoRotulo = "—";
        if (m.getVeiculo() != null) {
            if (m.getVeiculo().getSubtipoVeiculo() != null) {
                veiculoRotulo = m.getVeiculo().getSubtipoVeiculo().name();
            } else if (m.getVeiculo().getTipoVeiculo() != null) {
                veiculoRotulo = m.getVeiculo().getTipoVeiculo().name();
            }
        }
        return new ManifestoDestaqueDTO(
                m.getIdManifesto(),
                m.getValorRecebido(),
                m.getDataManifesto(),
                m.getDestino(),
                motoristaNome,
                veiculoRotulo);
    }
}

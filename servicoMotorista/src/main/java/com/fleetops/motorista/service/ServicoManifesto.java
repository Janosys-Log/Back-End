package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.domain.RendimentoMensal;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void SalvaOuNaoNoBancoComAsInformacoesDoManifesto(ManifestoDTO manifesto) {
        Manifesto manifestoParaSalvar = new Manifesto();
        if (manifestoRepo.existsById(manifesto.getManifestoid())) {
            return;
        }

        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

        manifestoParaSalvar.setAgregado(servAgregado.pegarPorCnpj(manifesto.getCnpjagregado()));
        manifestoParaSalvar.setDataManifesto(LocalDate.parse(manifesto.getData(), FMT));
        manifestoParaSalvar.setDestino(manifesto.getDestino());
        manifestoParaSalvar.setFreteAgregado(manifesto.getValorfreteagregado());
        manifestoParaSalvar.setFreteEmpresa(manifesto.getValorfreteempresa());
        manifestoParaSalvar.setIdManifesto(manifesto.getManifestoid());
        manifestoParaSalvar.setMotorista(servicoMotorista.pegarPorCPF(manifesto.getCpfmotorista()));
        manifestoParaSalvar.setObservacoes(manifesto.getObservacoes());
        manifestoParaSalvar.setVeiculo(sservicoVeiculo.acharPorPlaca(manifesto.getPlaca()));
        if (manifestoParaSalvar.getAgregado() == null
                || manifestoParaSalvar.getMotorista() == null
                || manifestoParaSalvar.getVeiculo() == null) {
            return;
        }
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

        return new RentabilidadeDTO(mes, ano, rentabilidadeMedia, crescimento, (long) listaMnifestos.size());
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
                    (long) rendimento.mes(), (long) rendimento.ano(), rendimento.rendimentoMedio(), crescimento, (long)
                            rendimento.numeroManifestos());
            map.put(rendimento.mes(), rentabilidade);
            anterior = rendimento.rendimentoMedio();
        }
        return map;
    }
}

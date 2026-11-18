package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.dto.CardsCustoDTO;
import com.fleetops.motorista.dto.CustoOperacionalDTO;
import com.fleetops.motorista.dto.ManifestoDestaqueDTO;
import com.fleetops.motorista.repository.ManifestoRepositorio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardsCustoService {

    private final ManifestoRepositorio repositorio;

    public CardsCustoService(ManifestoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public CardsCustoDTO calcular(YearMonth mesSolicitado) {
        YearMonth mes = mesSolicitado != null ? mesSolicitado : mesMaisRecente();
        if (mes == null) {
            return new CardsCustoDTO(null, true, null, null, null);
        }

        List<Manifesto> doMes = buscarMes(mes);
        if (doMes.isEmpty()) {
            return new CardsCustoDTO(mes.toString(), true, null, null, null);
        }
        List<Manifesto> doAnterior = buscarMes(mes.minusMonths(1));

        return new CardsCustoDTO(
                mes.toString(), false, custo(doMes, doAnterior), destaque(doMes, true), destaque(doMes, false));
    }

    private YearMonth mesMaisRecente() {
        List<LocalDate> datas = repositorio.listarDatasManifestosOrdenadasDesc();
        return datas.isEmpty() ? null : YearMonth.from(datas.get(0));
    }

    private List<Manifesto> buscarMes(YearMonth mes) {
        return repositorio.buscarPorPeriodoComMotoristaEVeiculo(mes.atDay(1), mes.atEndOfMonth());
    }

    static BigDecimal soma(List<Manifesto> lista) {
        return lista.stream().map(m -> nz(m.getFreteAgregado())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static BigDecimal rt(Manifesto m) {
        return nz(m.getFreteEmpresa()).subtract(nz(m.getFreteAgregado()));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    static BigDecimal media(BigDecimal total, long qtd) {
        return qtd == 0 ? null : total.divide(BigDecimal.valueOf(qtd), 2, RoundingMode.HALF_UP);
    }

    /** (atual - anterior) / anterior * 100; null se anterior ausente ou zero. */
    static BigDecimal variacao(BigDecimal atual, BigDecimal anterior) {
        if (atual == null || anterior == null || anterior.signum() == 0) {
            return null;
        }
        return atual.subtract(anterior).multiply(BigDecimal.valueOf(100)).divide(anterior, 1, RoundingMode.HALF_UP);
    }

    private CustoOperacionalDTO custo(List<Manifesto> mes, List<Manifesto> anterior) {
        BigDecimal total = soma(mes);
        BigDecimal medio = media(total, mes.size());

        boolean temAnterior = !anterior.isEmpty();
        BigDecimal totalAnt = temAnterior ? soma(anterior) : null;
        BigDecimal medioAnt = temAnterior ? media(totalAnt, anterior.size()) : null;

        return new CustoOperacionalDTO(
                total.setScale(2, RoundingMode.HALF_UP),
                totalAnt != null ? totalAnt.setScale(2, RoundingMode.HALF_UP) : null,
                variacao(total, totalAnt),
                medio,
                medioAnt,
                variacao(medio, medioAnt),
                mes.size());
    }

    /** Melhor = maior RT; pior = menor RT. Empate: mais recente, depois maior número. */
    ManifestoDestaqueDTO destaque(List<Manifesto> lista, boolean melhor) {
        Comparator<Manifesto> porRt = Comparator.comparing(CardsCustoService::rt);
        Comparator<Manifesto> desempate = Comparator.comparing(
                        Manifesto::getDataManifesto, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Manifesto::getIdManifesto);

        Manifesto escolhido = lista.stream()
                .max(melhor ? porRt.thenComparing(desempate) : porRt.reversed().thenComparing(desempate))
                .orElseThrow();

        String tipoVeiculo = null;
        if (escolhido.getVeiculo() != null) {
            var v = escolhido.getVeiculo();
            tipoVeiculo = v.getSubtipoVeiculo() != null
                    ? v.getSubtipoVeiculo().name()
                    : (v.getTipoVeiculo() != null ? v.getTipoVeiculo().name() : null);
        }

        return new ManifestoDestaqueDTO(
                escolhido.getIdManifesto(),
                rt(escolhido).setScale(2, RoundingMode.HALF_UP),
                escolhido.getDataManifesto(),
                escolhido.getDestino(),
                escolhido.getMotorista() != null ? escolhido.getMotorista().getNomeMotorista() : null,
                tipoVeiculo);
    }
}

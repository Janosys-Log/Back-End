package com.fleetops.motorista.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.domain.Motorista;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CardsCustoServiceTest {

    private final CardsCustoService service = new CardsCustoService(null);

    private Manifesto m(long id, String empresa, String agregado, LocalDate data) {
        return Manifesto.builder()
                .idManifesto(id)
                .dataManifesto(data)
                .destino("Santos")
                .freteEmpresa(new BigDecimal(empresa))
                .freteAgregado(new BigDecimal(agregado))
                .motorista(Motorista.builder().nomeMotorista("Fulano").build())
                .build();
    }

    private Manifesto m(long id, String empresa, String agregado) {
        return m(id, empresa, agregado, LocalDate.of(2026, 9, 1));
    }

    @Test
    void custoOperacionalSomaFreteDoAgregado() {
        var lista = List.of(m(1, "1000", "700"), m(2, "1200", "800"));
        assertEquals(0, new BigDecimal("1500").compareTo(CardsCustoService.soma(lista)));
    }

    @Test
    void rtDoManifestoPodeSerNegativa() {
        assertEquals(0, new BigDecimal("-100").compareTo(CardsCustoService.rt(m(1, "1200", "1300"))));
    }

    @Test
    void custoMedioDividePorManifestos() {
        assertEquals(new BigDecimal("750.00"), CardsCustoService.media(new BigDecimal("1500"), 2));
    }

    @Test
    void variacaoPercentual() {
        assertEquals(
                new BigDecimal("1.5"),
                CardsCustoService.variacao(new BigDecimal("2648.20"), new BigDecimal("2609.00")));
        assertEquals(
                new BigDecimal("7.2"),
                CardsCustoService.variacao(new BigDecimal("861578.40"), new BigDecimal("803619.00")));
    }

    @Test
    void variacaoNulaSemMesAnteriorOuZero() {
        assertNull(CardsCustoService.variacao(BigDecimal.TEN, null));
        assertNull(CardsCustoService.variacao(BigDecimal.TEN, BigDecimal.ZERO));
    }

    @Test
    void melhorEPiorManifestoDoMes() {
        var lista = List.of(m(1, "1150", "1000"), m(2, "20", "1000"), m(3, "5250", "1000"));
        assertEquals(3L, service.destaque(lista, true).numero());
        assertEquals(
                0,
                new BigDecimal("4250").compareTo(service.destaque(lista, true).rt()));
        assertEquals(2L, service.destaque(lista, false).numero());
        assertEquals(
                0,
                new BigDecimal("-980").compareTo(service.destaque(lista, false).rt()));
    }

    @Test
    void empateDeRtVaiParaOMaisRecenteDepoisMaiorNumero() {
        var lista = List.of(
                m(10, "200", "100", LocalDate.of(2026, 9, 1)),
                m(11, "200", "100", LocalDate.of(2026, 9, 5)),
                m(12, "200", "100", LocalDate.of(2026, 9, 5)));
        assertEquals(12L, service.destaque(lista, true).numero());
    }

    @Test
    void manifestoUnicoEMelhorEPior() {
        var lista = List.of(m(7, "300", "100"));
        assertEquals(7L, service.destaque(lista, true).numero());
        assertEquals(7L, service.destaque(lista, false).numero());
    }
}

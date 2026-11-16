package com.fleetops.motorista.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.SubtipoVeiculo;
import com.fleetops.motorista.domain.TipoVeiculo;
import com.fleetops.motorista.domain.Veiculo;
import com.fleetops.motorista.dto.ranking.GraficoDestinoComparativoDTO;
import com.fleetops.motorista.dto.ranking.GraficoFaixaItemDTO;
import com.fleetops.motorista.dto.ranking.GraficoSubcategoriaItemDTO;
import com.fleetops.motorista.dto.ranking.RankingMotoristaItemDTO;
import com.fleetops.motorista.dto.ranking.RankingRentabilidadeResponseDTO;
import com.fleetops.motorista.repository.ManifestoRepositorio;
import com.fleetops.motorista.util.DestinoGrandeSpUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentabilidadeServiceTest {

    @Mock
    private ManifestoRepositorio manifestoRepositorio;

    private RentabilidadeService rentabilidadeService;

    @BeforeEach
    void setUp() {
        rentabilidadeService = new RentabilidadeService(manifestoRepositorio);
    }

    @Test
    @DisplayName("CA4 - Exemplo numérico oficial: RT = 700,00, NM = 2, RM = 350,00 e Margem = 31,8%")
    void deveCalcularFormulaOficialRentabilidadeCorretamente() {
        Veiculo veiculo = criarVeiculo(1L, TipoVeiculo.URBANOS_DE_CARGA, SubtipoVeiculo.VUC);
        Motorista motorista = criarMotorista(10L, "Carlos Silva", "12345678901", veiculo);

        Manifesto m1 = criarManifesto(
                1001L,
                LocalDate.of(2026, 9, 5),
                "SAO PAULO",
                new BigDecimal("1000.00"),
                new BigDecimal("600.00"),
                motorista,
                veiculo);
        Manifesto m2 = criarManifesto(
                1002L,
                LocalDate.of(2026, 9, 10),
                "GUARULHOS",
                new BigDecimal("1200.00"),
                new BigDecimal("900.00"),
                motorista,
                veiculo);

        when(manifestoRepositorio.listarDatasManifestosOrdenadasDesc()).thenReturn(List.of(LocalDate.of(2026, 9, 10)));
        when(manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                        LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))
                .thenReturn(List.of(m1, m2));
        when(manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                        LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31)))
                .thenReturn(List.of());

        RankingRentabilidadeResponseDTO resposta = rentabilidadeService.obterRankingModal("2026-09", "GERAL");

        assertEquals(1, resposta.itens().size());
        RankingMotoristaItemDTO item = resposta.itens().get(0);
        assertEquals(1, item.posicao());
        assertEquals(2, item.numeroManifestos());
        assertEquals(new BigDecimal("700.00"), item.rentabilidadeTotal());
        assertEquals(new BigDecimal("350.00"), item.rentabilidadeMedia());
        assertEquals(new BigDecimal("31.8"), item.margemPercentual());
        assertEquals("SEM_MES_ANTERIOR", item.statusVariacao());
    }

    @Test
    @DisplayName("CA3 - Normalização de destinos da Grande SP deve reconhecer acentos, caixa e sufixos")
    void deveNormalizarEClassificarDestinosGrandeSp() {
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("SAO PAULO"));
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("São Paulo"));
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("  são caetano do sul - sp "));
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("SAO CAETANO"));
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("Santo André/SP"));
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("SAO BERNARDO DO CAMPO"));
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("São Bernardo"));
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("Diadema (SP)"));
        assertTrue(DestinoGrandeSpUtil.isGrandeSp("GUARULHOS"));

        assertFalse(DestinoGrandeSpUtil.isGrandeSp("CAMPINAS"));
        assertFalse(DestinoGrandeSpUtil.isGrandeSp("SANTOS"));
        assertFalse(DestinoGrandeSpUtil.isGrandeSp("OSASCO"));
        assertFalse(DestinoGrandeSpUtil.isGrandeSp(null));
        assertFalse(DestinoGrandeSpUtil.isGrandeSp("   "));
    }

    @Test
    @DisplayName("CA6 - Desempate oficial: maior RM -> maior NM -> manifesto mais recente -> ordem alfabética")
    void deveAplicarCriteriosDeDesempateNaOrdemCorreta() {
        Veiculo veiculo = criarVeiculo(1L, TipoVeiculo.URBANOS_EXPRESS, SubtipoVeiculo.VAN);
        Motorista motA = criarMotorista(1L, "Ana Souza", "111", veiculo);
        Motorista motB = criarMotorista(2L, "Bruno Lima", "222", veiculo);
        Motorista motC = criarMotorista(3L, "Daniel Costa", "333", veiculo);
        Motorista motD = criarMotorista(4L, "Eduardo Rocha", "444", veiculo);

        // Todos com RM = 500.00:
        // motC tem 2 manifestos de RT 500 -> NM = 2 (deve ficar em 1º)
        Manifesto c1 = criarManifesto(
                10L,
                LocalDate.of(2026, 9, 2),
                "SAO PAULO",
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                motC,
                veiculo);
        Manifesto c2 = criarManifesto(
                11L,
                LocalDate.of(2026, 9, 3),
                "SAO PAULO",
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                motC,
                veiculo);

        // motD tem 1 manifesto porém data mais recente (20/09) -> deve ficar em 2º
        Manifesto d1 = criarManifesto(
                12L,
                LocalDate.of(2026, 9, 20),
                "SAO PAULO",
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                motD,
                veiculo);

        // motA e motB têm 1 manifesto na mesma data (10/09) e mesmo ID de manifesto -> desempate alfabético
        Manifesto a1 = criarManifesto(
                20L,
                LocalDate.of(2026, 9, 10),
                "SAO PAULO",
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                motA,
                veiculo);
        Manifesto b1 = criarManifesto(
                20L,
                LocalDate.of(2026, 9, 10),
                "SAO PAULO",
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                motB,
                veiculo);

        when(manifestoRepositorio.listarDatasManifestosOrdenadasDesc()).thenReturn(List.of(LocalDate.of(2026, 9, 20)));
        when(manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                        LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))
                .thenReturn(List.of(b1, a1, d1, c1, c2));
        when(manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                        LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31)))
                .thenReturn(List.of());

        RankingRentabilidadeResponseDTO resposta = rentabilidadeService.obterRankingModal("2026-09", "GERAL");

        List<RankingMotoristaItemDTO> ranking = resposta.itens();
        assertEquals(4, ranking.size());
        assertEquals("Daniel Costa", ranking.get(0).nomeMotorista());
        assertEquals("Eduardo Rocha", ranking.get(1).nomeMotorista());
        assertEquals("Ana Souza", ranking.get(2).nomeMotorista());
        assertEquals("Bruno Lima", ranking.get(3).nomeMotorista());
    }

    @Test
    @DisplayName("CA5 e CA8/CA9/CA10 - Variação vs mês anterior e cálculo dos 3 gráficos laterais")
    void deveCalcularVariacaoEConstruirOsTresGraficosCorretamente() {
        Veiculo vuc = criarVeiculo(1L, TipoVeiculo.URBANOS_DE_CARGA, SubtipoVeiculo.VUC);
        Veiculo truck = criarVeiculo(2L, TipoVeiculo.PESADOS, SubtipoVeiculo.TRUCK);

        Motorista mPositivo = criarMotorista(1L, "Motorista Top", "111", truck);
        Motorista mNegativo = criarMotorista(2L, "Motorista Negativo", "222", vuc);

        // Mês atual (2026-09):
        // mPositivo: 1 manifesto em CAMPINAS (Demais destinos) com RT = 600.00 -> RM = 600.00 (faixa 500-1000)
        Manifesto atual1 = criarManifesto(
                100L,
                LocalDate.of(2026, 9, 12),
                "CAMPINAS",
                new BigDecimal("1600.00"),
                new BigDecimal("1000.00"),
                mPositivo,
                truck);
        // mNegativo: 1 manifesto em SAO PAULO (Grande SP) com RT = -200.00 -> RM = -200.00 (faixa < 0)
        Manifesto atual2 = criarManifesto(
                101L,
                LocalDate.of(2026, 9, 15),
                "SAO PAULO",
                new BigDecimal("500.00"),
                new BigDecimal("700.00"),
                mNegativo,
                vuc);

        // Mês anterior (2026-08): apenas mPositivo operou com RM = 500.00
        Manifesto ant1 = criarManifesto(
                90L,
                LocalDate.of(2026, 8, 10),
                "CAMPINAS",
                new BigDecimal("1500.00"),
                new BigDecimal("1000.00"),
                mPositivo,
                truck);

        when(manifestoRepositorio.listarDatasManifestosOrdenadasDesc())
                .thenReturn(List.of(LocalDate.of(2026, 9, 15), LocalDate.of(2026, 8, 10)));
        when(manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                        LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))
                .thenReturn(List.of(atual1, atual2));
        when(manifestoRepositorio.buscarPorPeriodoComMotoristaEVeiculo(
                        LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31)))
                .thenReturn(List.of(ant1));

        RankingRentabilidadeResponseDTO resp = rentabilidadeService.obterRankingModal("2026-09", "GERAL");

        // Resumo: média da frota = (600 - 200) / 2 = 200.00
        assertEquals(new BigDecimal("200.00"), resp.resumo().mediaFrotaMes());
        assertEquals(new BigDecimal("500.00"), resp.resumo().mediaFrotaMesAnterior());
        assertEquals(new BigDecimal("-60.0"), resp.resumo().variacaoFrotaPercentual());
        assertEquals(1, resp.resumo().totalMotoristasNegativos());

        // Variação individual
        RankingMotoristaItemDTO topItem = resp.itens().get(0);
        assertEquals("AUMENTO", topItem.statusVariacao());
        assertEquals(new BigDecimal("100.00"), topItem.variacaoMesAnterior());

        RankingMotoristaItemDTO negItem = resp.itens().get(1);
        assertEquals("NOVO", negItem.statusVariacao());
        assertNull(negItem.variacaoMesAnterior());
        assertTrue(negItem.rentabilidadeNegativa());
        assertEquals("VERMELHO", negItem.corDestaque());

        // Gráfico 1: 8 faixas de R$ 500
        List<GraficoFaixaItemDTO> faixas = resp.graficoFaixas();
        assertEquals(8, faixas.size());
        assertEquals(1, faixas.get(0).quantidadeMotoristas()); // < 0
        assertEquals("VERMELHO", faixas.get(0).cor());
        assertTrue(faixas.get(1).contemMediaFrota()); // 200.00 está em [0, 500)
        assertEquals(1, faixas.get(2).quantidadeMotoristas()); // 600.00 está em [500, 1000)

        // Gráfico 2: Subcategorias
        List<GraficoSubcategoriaItemDTO> subcats = resp.graficoSubcategorias();
        assertEquals(2, subcats.size());

        // Gráfico 3: Grande SP vs Demais destinos
        GraficoDestinoComparativoDTO dest = resp.graficoGrandeSpVsDemais();
        assertNotNull(dest);
        assertEquals(new BigDecimal("-200.00"), dest.rentabilidadeMediaGrandeSp());
        assertEquals(1, dest.manifestosGrandeSp());
        assertEquals(new BigDecimal("600.00"), dest.rentabilidadeMediaDemaisDestinos());
        assertEquals(1, dest.manifestosDemaisDestinos());
    }

    private Veiculo criarVeiculo(Long id, TipoVeiculo tipo, SubtipoVeiculo subtipo) {
        Veiculo v = new Veiculo();
        v.setIdVeiculo(id);
        v.setPlacaVeiculo("ABC1D23");
        v.setTipoVeiculo(tipo);
        v.setSubtipoVeiculo(subtipo);
        return v;
    }

    private Motorista criarMotorista(Long id, String nome, String cpf, Veiculo veiculo) {
        Motorista m = new Motorista();
        m.setIdMotorista(id);
        m.setNomeMotorista(nome);
        m.setCpfMotorista(cpf);
        m.setVeiculo(veiculo);
        return m;
    }

    private Manifesto criarManifesto(
            Long id,
            LocalDate data,
            String destino,
            BigDecimal freteEmpresa,
            BigDecimal freteAgregado,
            Motorista motorista,
            Veiculo veiculo) {
        Manifesto mf = new Manifesto();
        mf.setIdManifesto(id);
        mf.setDataManifesto(data);
        mf.setDestino(destino);
        mf.setFreteEmpresa(freteEmpresa);
        mf.setFreteAgregado(freteAgregado);
        mf.setMotorista(motorista);
        mf.setVeiculo(veiculo);
        return mf;
    }
}

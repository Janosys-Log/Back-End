package com.fleetops.motorista.dto.ranking;

import java.util.List;

public record RankingRentabilidadeResponseDTO(
        ResumoRankingDTO resumo,
        List<RankingMotoristaItemDTO> itens,
        List<GraficoFaixaItemDTO> graficoFaixas,
        List<GraficoSubcategoriaItemDTO> graficoSubcategorias,
        GraficoDestinoComparativoDTO graficoGrandeSpVsDemais,
        List<String> mesesDisponiveis) {}

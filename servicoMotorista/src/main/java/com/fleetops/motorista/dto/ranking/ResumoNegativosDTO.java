package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;

public record ResumoNegativosDTO(
        String mesReferencia,
        String mesAnterior,
        VisaoRanking visao,
        int quantidadeMotoristasNegativos,
        int totalMotoristasFrota,
        BigDecimal percentualNegativos,
        BigDecimal rtTotalNegativos,
        boolean possuiDadosMes) {}

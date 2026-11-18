package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;

public record ResumoRankingDTO(
        String mesSelecionado,
        String mesAnterior,
        VisaoRanking visao,
        BigDecimal mediaFrotaMes,
        BigDecimal mediaFrotaMesAnterior,
        BigDecimal variacaoFrotaPercentual,
        int totalMotoristas,
        int totalMotoristasNegativos,
        int totalManifestos,
        boolean possuiDadosMes,
        boolean possuiManifestosImportadosNoMes,
        boolean possuiDadosMesAnterior) {}

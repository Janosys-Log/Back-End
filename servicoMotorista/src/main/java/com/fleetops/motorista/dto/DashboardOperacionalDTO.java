package com.fleetops.motorista.dto;

import java.math.BigDecimal;

public record DashboardOperacionalDTO(
        String mesReferencia,
        String mesAnterior,
        BigDecimal custoOperacionalTotal,
        BigDecimal custoOperacionalTotalMesAnterior,
        BigDecimal variacaoCustoTotalPercentual,
        BigDecimal custoMedioPorManifesto,
        BigDecimal custoMedioPorManifestoMesAnterior,
        BigDecimal variacaoCustoMedioPercentual,
        Long totalManifestos,
        ManifestoDestaqueDTO melhorManifesto,
        ManifestoDestaqueDTO piorManifesto) {}

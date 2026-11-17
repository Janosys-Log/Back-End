package com.fleetops.motorista.dto;

import java.math.BigDecimal;

public record CustoOperacionalDTO(
        BigDecimal total,
        BigDecimal totalMesAnterior,
        BigDecimal variacaoTotalPercentual,
        BigDecimal custoMedio,
        BigDecimal custoMedioMesAnterior,
        BigDecimal variacaoCustoMedioPercentual,
        long numeroManifestos) {}

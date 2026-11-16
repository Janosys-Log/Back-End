package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;

public record GraficoFaixaItemDTO(
        String codigoFaixa,
        String rotulo,
        BigDecimal limiteInferior,
        BigDecimal limiteSuperior,
        int quantidadeMotoristas,
        String cor,
        boolean contemMediaFrota) {}

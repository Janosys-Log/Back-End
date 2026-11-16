package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;

public record GraficoSubcategoriaItemDTO(
        String subtipoVeiculo,
        String subtipoLabel,
        int quantidadeMotoristas,
        int quantidadeManifestos,
        BigDecimal rentabilidadeMedia,
        String cor) {}

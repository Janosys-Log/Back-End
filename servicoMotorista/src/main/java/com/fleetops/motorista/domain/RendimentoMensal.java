package com.fleetops.motorista.domain;

import java.math.BigDecimal;

public record RendimentoMensal(
        Integer ano, Integer mes, BigDecimal rendimentoMedio, Long numeroManifestos, BigDecimal rendimentoTotal) {}

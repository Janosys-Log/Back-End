package com.fleetops.motorista.dto;

import java.math.BigDecimal;

public record ManifestoDestaqueDTO(
        long numero, BigDecimal rt, String data, String destino, String motorista, String tipoVeiculo) {}

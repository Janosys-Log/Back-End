package com.fleetops.motorista.dto;

import java.math.BigDecimal;

public record ManifestoDestaqueDTO(
        Long numero, BigDecimal rt, java.time.LocalDate data, String destino, String motorista, String tipoVeiculo) {}

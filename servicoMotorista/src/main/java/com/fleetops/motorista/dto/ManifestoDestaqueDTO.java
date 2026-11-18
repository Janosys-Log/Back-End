package com.fleetops.motorista.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ManifestoDestaqueDTO(
        Long idManifesto,
        BigDecimal valorRecebido,
        LocalDate dataManifesto,
        String destino,
        String motoristaNome,
        String veiculoDescricao) {}

package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;

public record ManifestoItemDetalheDTO(
        Long idManifesto,
        String numeroManifesto,
        String dataManifesto,
        String destino,
        BigDecimal freteEmpresa,
        BigDecimal freteMotorista,
        BigDecimal rtManifesto) {}

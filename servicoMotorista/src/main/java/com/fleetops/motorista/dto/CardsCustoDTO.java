package com.fleetops.motorista.dto;

public record CardsCustoDTO(
        String mes,
        boolean semDados,
        CustoOperacionalDTO custoOperacional,
        ManifestoDestaqueDTO melhorManifesto,
        ManifestoDestaqueDTO piorManifesto) {}

package com.fleetops.motorista.domain;

import java.text.Normalizer;
import lombok.Getter;

@Getter
public enum TipoVeiculo {
    URBANOS_EXPRESS("Urbanos Express"),
    URBANOS_DE_CARGA("Urbanos de Carga"),
    PESADOS("Pesados"),
    ARTICULADOS("Articulados");

    private final String descricao;

    TipoVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public static TipoVeiculo fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized = Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        if (normalized.contains("express")) {
            return URBANOS_EXPRESS;
        }
        if (normalized.contains("carga")) {
            return URBANOS_DE_CARGA;
        }
        if (normalized.contains("pesado")) {
            return PESADOS;
        }
        if (normalized.contains("articulado")) {
            return ARTICULADOS;
        }

        SubtipoVeiculo subtipo = SubtipoVeiculo.fromString(text);
        if (subtipo != null) {
            return subtipo.getTipo();
        }

        for (TipoVeiculo tipo : values()) {
            if (tipo.name().equalsIgnoreCase(text.trim()) || tipo.getDescricao().equalsIgnoreCase(text.trim())) {
                return tipo;
            }
        }
        return null;
    }
}

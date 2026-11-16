package com.fleetops.motorista.dto.ranking;

import java.util.Locale;

public enum VisaoRanking {
    GERAL,
    GRANDE_SP;

    public static VisaoRanking fromParam(String valor) {
        if (valor == null || valor.isBlank()) {
            return GERAL;
        }
        String normalizado =
                valor.trim().toUpperCase(Locale.ROOT).replace("-", "_").replace(" ", "_");
        if ("GRANDE_SP".equals(normalizado) || "GRANDESP".equals(normalizado)) {
            return GRANDE_SP;
        }
        return GERAL;
    }
}

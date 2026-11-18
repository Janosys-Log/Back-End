package com.fleetops.motorista.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

/**
 * Utilitário para classificar destinos pertencentes à rota Grande SP,
 * normalizando acentuação, caixa alta/baixa e variações de grafia.
 */
public final class DestinoGrandeSpUtil {

    private static final Set<String> MUNICIPIOS_GRANDE_SP = Set.of(
            "SAO PAULO",
            "SANTO ANDRE",
            "SAO BERNARDO DO CAMPO",
            "SAO BERNARDO",
            "SAO CAETANO",
            "SAO CAETANO DO SUL",
            "DIADEMA",
            "GUARULHOS");

    private DestinoGrandeSpUtil() {}

    public static boolean isGrandeSp(String destino) {
        if (destino == null || destino.isBlank()) {
            return false;
        }
        String normalizado = normalizarDestino(destino);
        return MUNICIPIOS_GRANDE_SP.contains(normalizado);
    }

    public static String normalizarDestino(String destino) {
        if (destino == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(destino, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String upper = semAcento.toUpperCase(Locale.ROOT).trim();
        // Remove sufixos comuns de UF como "- SP", "/SP", "(SP)"
        String semUf = upper.replaceAll("\\s*(?:[-/,]|\\()\\s*SP\\)?$", "").trim();
        return semUf.replaceAll("\\s+", " ");
    }
}

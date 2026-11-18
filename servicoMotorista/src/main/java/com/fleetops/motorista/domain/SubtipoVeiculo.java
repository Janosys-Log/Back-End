package com.fleetops.motorista.domain;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum SubtipoVeiculo {

    // === URBANOS EXPRESS ===
    MOTOCICLETAS("Motocicletas", TipoVeiculo.URBANOS_EXPRESS),
    AUTOMOVEIS("Automóveis", TipoVeiculo.URBANOS_EXPRESS),
    FIORINO("Fiorino", TipoVeiculo.URBANOS_EXPRESS),
    VAN("Van", TipoVeiculo.URBANOS_EXPRESS),

    // === URBANOS DE CARGA ===
    VUC("VUC", TipoVeiculo.URBANOS_DE_CARGA),
    TRES_QUARTOS("3/4", TipoVeiculo.URBANOS_DE_CARGA),

    // === PESADOS ===
    TOCO("Toco ou Semi-pesado", TipoVeiculo.PESADOS),
    TRUCK("Truck ou Pesado", TipoVeiculo.PESADOS),
    BI_TRUCK("Bi-truck ou Pesado", TipoVeiculo.PESADOS),

    // === ARTICULADOS ===
    CAVALO_MECANICO("Cavalo Mecânico", TipoVeiculo.ARTICULADOS),
    CAVALO_MECANICO_TRUCADO("Cavalo Mecânico Trucado", TipoVeiculo.ARTICULADOS),
    SEMI_REBOQUE_2_EIXOS("Semi-reboque com 02 Eixos", TipoVeiculo.ARTICULADOS),
    SEMI_REBOQUE_3_EIXOS("Semi-reboque com 03 Eixos", TipoVeiculo.ARTICULADOS),
    SEMI_REBOQUE_4_EIXOS("Semi-reboque com 04 Eixos", TipoVeiculo.ARTICULADOS);

    private final String descricao;
    private final TipoVeiculo tipo;

    SubtipoVeiculo(String descricao, TipoVeiculo tipo) {
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public static List<SubtipoVeiculo> listarPorTipo(TipoVeiculo tipo) {
        return Arrays.stream(values())
                .filter(subtipo -> subtipo.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    public static SubtipoVeiculo fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized = Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        if (normalized.equals("3/4") || normalized.equals("03/abr") || normalized.equals("tres quartos")) {
            return TRES_QUARTOS;
        }
        if (normalized.equals("fiorino")) {
            return FIORINO;
        }
        if (normalized.equals("van")) {
            return VAN;
        }
        if (normalized.equals("vuc")) {
            return VUC;
        }
        if (normalized.contains("motocicleta")) {
            return MOTOCICLETAS;
        }
        if (normalized.contains("automovel") || normalized.contains("automoveis")) {
            return AUTOMOVEIS;
        }
        if (normalized.contains("bi-truck") || normalized.contains("bitruck")) {
            return BI_TRUCK;
        }
        if (normalized.contains("truck")) {
            return TRUCK;
        }
        if (normalized.contains("toco")) {
            return TOCO;
        }
        if (normalized.contains("trucado")) {
            return CAVALO_MECANICO_TRUCADO;
        }
        if (normalized.contains("cavalo")) {
            return CAVALO_MECANICO;
        }
        if (normalized.contains("2 eixos") || normalized.contains("02 eixos")) {
            return SEMI_REBOQUE_2_EIXOS;
        }
        if (normalized.contains("3 eixos") || normalized.contains("03 eixos")) {
            return SEMI_REBOQUE_3_EIXOS;
        }
        if (normalized.contains("4 eixos") || normalized.contains("04 eixos")) {
            return SEMI_REBOQUE_4_EIXOS;
        }

        for (SubtipoVeiculo subtipo : values()) {
            if (subtipo.name().equalsIgnoreCase(text.trim())
                    || subtipo.getDescricao().equalsIgnoreCase(text.trim())) {
                return subtipo;
            }
        }
        return null;
    }
}

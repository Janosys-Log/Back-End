package com.fleetops.servicoMotorista.domain;

public enum TipoVeiculos {
    FIORINO(CategoriaVeiculo.URBANOS_EXPRESS),
    VAN(CategoriaVeiculo.URBANOS_EXPRESS),
    HR(CategoriaVeiculo.URBANOS_EXPRESS),
    UTILITARIO(CategoriaVeiculo.URBANOS_EXPRESS),
    VUC(CategoriaVeiculo.URBANOS_DE_CARGA),
    TRES_QUARTOS(CategoriaVeiculo.URBANOS_DE_CARGA),
    TOCO(CategoriaVeiculo.URBANOS_DE_CARGA),
    TRUCK_LEVE(CategoriaVeiculo.URBANOS_DE_CARGA),
    TRUCK(CategoriaVeiculo.PESADOS),
    CARRETA(CategoriaVeiculo.PESADOS),
    BITRUCK(CategoriaVeiculo.PESADOS),
    CAVALO(CategoriaVeiculo.PESADOS),
    BITREM(CategoriaVeiculo.ARTICULADOS),
    RODOTREM(CategoriaVeiculo.ARTICULADOS);

    private final CategoriaVeiculo categoria;

    TipoVeiculos(CategoriaVeiculo categoria) {
        this.categoria = categoria;
    }

    public CategoriaVeiculo getCategoria() {
        return categoria;
    }

    public static TipoVeiculos fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tipo de veículo não informado");
        }
        String normalized = value.trim()
                .toUpperCase()
                .replace(" ", "_")
                .replace("-", "_")
                .replace("Ç", "C")
                .replace("Ã", "A")
                .replace("Õ", "O");
        switch (normalized) {
            case "FIORINO":
                return FIORINO;
            case "VAN":
            case "FURGAO":
            case "FURGÃO":
                return VAN;
            case "HR":
                return HR;
            case "UTILITARIO":
            case "UTILITÁRIO":
                return UTILITARIO;
            case "VUC":
            case "VEICULO_URBANO_DE_CARGA":
                return VUC;
            case "TRES_QUARTOS":
            case "3/4":
            case "3_4":
            case "TRESQUARTOS":
                return TRES_QUARTOS;
            case "TOCO":
                return TOCO;
            case "TRUCK_LEVE":
            case "TRUCKLEVE":
                return TRUCK_LEVE;
            case "TRUCK":
                return TRUCK;
            case "CARRETA":
                return CARRETA;
            case "BITRUCK":
            case "BI_TRUCK":
                return BITRUCK;
            case "CAVALO":
            case "CAVALO_MECANICO":
            case "CAVALO_MECÂNICO":
                return CAVALO;
            case "BITREM":
                return BITREM;
            case "RODOTREM":
                return RODOTREM;
            default:
                throw new IllegalArgumentException("Tipo de veículo desconhecido: " + value);
        }
    }
}

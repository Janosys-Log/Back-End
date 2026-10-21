package com.fleetops.motorista.dto;

import com.fleetops.motorista.domain.SubtipoVeiculo;
import com.fleetops.motorista.domain.TipoVeiculo;
import com.fleetops.motorista.domain.Veiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VeiculoDTO(

        Long idVeiculo,

        @NotBlank(message = "A placa do veículo é obrigatória")
        @Size(max = 7, message = "A placa deve ter no máximo 7 caracteres")
        String placaVeiculo,

        TipoVeiculo tipoVeiculo,

        SubtipoVeiculo subtipoVeiculo,

        @NotNull(message = "O agregado proprietário é obrigatório")
        Long agregadoId
) {

    public static VeiculoDTO fromEntity(Veiculo veiculo) {
        return new VeiculoDTO(
                veiculo.getIdVeiculo(),
                veiculo.getPlacaVeiculo(),
                veiculo.getTipoVeiculo(),
                veiculo.getSubtipoVeiculo(),
                veiculo.getAgregadoId()
        );
    }

    public Veiculo toEntity() {
        return Veiculo.builder()
                .idVeiculo(this.idVeiculo)
                .placaVeiculo(this.placaVeiculo)
                .tipoVeiculo(this.tipoVeiculo)
                .subtipoVeiculo(this.subtipoVeiculo)
                .agregadoId(this.agregadoId)
                .build();
    }
}
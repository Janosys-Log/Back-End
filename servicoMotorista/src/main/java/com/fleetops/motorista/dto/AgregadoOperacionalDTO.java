package com.fleetops.motorista.dto;

import com.fleetops.motorista.domain.Agregado;
import java.util.Set;
import java.util.stream.Collectors;

/** Resposta de agregado para o perfil Operacional, com motoristas sem nota média. */
public record AgregadoOperacionalDTO(
        Long idAgregado,
        String nomeAgregado,
        String cnpjAgregado,
        String contatoAgregado,
        Set<MotoristaOperacionalDTO> motoristas,
        Set<VeiculoDTO> veiculos) {

    public static AgregadoOperacionalDTO fromEntity(Agregado agregado) {
        return new AgregadoOperacionalDTO(
                agregado.getIdAgregado(),
                agregado.getNomeAgregado(),
                agregado.getCnpjAgregado(),
                agregado.getContatoAgregado(),
                agregado.getMotoristas().stream()
                        .map(MotoristaOperacionalDTO::fromEntity)
                        .collect(Collectors.toSet()),
                agregado.getVeiculos().stream()
                        .map(VeiculoDTO::fromEntity)
                        .collect(Collectors.toSet()));
    }
}

package com.fleetops.motorista.dto;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import java.time.LocalDateTime;

/** Resposta de motorista para o perfil Operacional, sem métricas de avaliação. */
public record MotoristaOperacionalDTO(
        Long idMotorista,
        String nomeMotorista,
        String cpfMotorista,
        String contatoMotorista,
        StatusMotorista status,
        LocalDateTime statusUpdatedAt,
        Long manifestoAtual,
        Integer contadorRotaSp,
        VeiculoDTO veiculo,
        Long agregadoId) {

    public static MotoristaOperacionalDTO fromEntity(Motorista motorista) {
        return new MotoristaOperacionalDTO(
                motorista.getIdMotorista(),
                motorista.getNomeMotorista(),
                motorista.getCpfMotorista(),
                motorista.getContatoMotorista(),
                motorista.getStatus(),
                motorista.getStatusUpdatedAt(),
                motorista.getManifestoAtual(),
                motorista.getContadorRotaSp(),
                motorista.getVeiculo() != null ? VeiculoDTO.fromEntity(motorista.getVeiculo()) : null,
                motorista.getAgregadoId());
    }
}

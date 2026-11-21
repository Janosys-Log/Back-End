package com.fleetops.motorista.dto;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.domain.TipoContato;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Resposta de motorista para o perfil Operacional, sem métricas de avaliação. */
public record MotoristaOperacionalDTO(
        Long idMotorista,
        String nomeMotorista,
        String cpfMotorista,
        String contatoMotorista,
        TipoContato tipoContato,
        String contatoSecundario,
        TipoContato tipoContatoSecundario,
        StatusMotorista status,
        LocalDateTime statusUpdatedAt,
        Long manifestoAtual,
        LocalDate dataUltimoManifesto,
        Integer contadorRotaSp,
        VeiculoDTO veiculo,
        Long agregadoId) {

    public static MotoristaOperacionalDTO fromEntity(Motorista motorista) {
        return new MotoristaOperacionalDTO(
                motorista.getIdMotorista(),
                motorista.getNomeMotorista(),
                motorista.getCpfMotorista(),
                motorista.getContatoMotorista(),
                motorista.getTipoContato(),
                motorista.getContatoSecundario(),
                motorista.getTipoContatoSecundario(),
                motorista.getStatus(),
                motorista.getStatusUpdatedAt(),
                motorista.getManifestoAtual(),
                motorista.getDataUltimoManifesto(),
                motorista.getContadorRotaSp(),
                motorista.getVeiculo() != null ? VeiculoDTO.fromEntity(motorista.getVeiculo()) : null,
                motorista.getAgregadoId());
    }
}

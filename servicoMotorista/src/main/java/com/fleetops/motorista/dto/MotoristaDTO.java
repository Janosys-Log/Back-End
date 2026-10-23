package com.fleetops.motorista.dto;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record MotoristaDTO(
        Long idMotorista,
        @NotBlank(message = "O nome do motorista é obrigatório")
                @Size(max = 100, message = "O nome do motorista deve ter no máximo 100 caracteres")
                String nomeMotorista,
        @NotBlank(message = "O CPF do motorista é obrigatório")
                @Size(max = 11, message = "O CPF deve ter no máximo 11 dígitos numéricos")
                String cpfMotorista,
        @Size(max = 20, message = "O contato deve ter no máximo 20 caracteres") String contatoMotorista,
        StatusMotorista status,
        LocalDateTime statusUpdatedAt,
        Integer contadorRotaSp,
        Float notaMedia,
        VeiculoDTO veiculo,
        @NotNull(message = "O agregado ao qual o motorista pertence é obrigatório") Long agregadoId) {

    public static MotoristaDTO fromEntity(Motorista motorista) {
        return new MotoristaDTO(
                motorista.getIdMotorista(),
                motorista.getNomeMotorista(),
                motorista.getCpfMotorista(),
                motorista.getContatoMotorista(),
                motorista.getStatus(),
                motorista.getStatusUpdatedAt(),
                motorista.getContadorRotaSp(),
                motorista.getNotaMedia(),
                motorista.getVeiculo() != null ? VeiculoDTO.fromEntity(motorista.getVeiculo()) : null,
                motorista.getAgregadoId());
    }

    public Motorista toEntity() {
        return Motorista.builder()
                .idMotorista(this.idMotorista)
                .nomeMotorista(this.nomeMotorista)
                .cpfMotorista(this.cpfMotorista)
                .contatoMotorista(this.contatoMotorista)
                .status(this.status != null ? this.status : StatusMotorista.Disponivel)
                .statusUpdatedAt(this.statusUpdatedAt)
                .contadorRotaSp(this.contadorRotaSp != null ? this.contadorRotaSp : 0)
                .notaMedia(this.notaMedia != null ? this.notaMedia : 0.0f)
                .veiculo(this.veiculo != null ? this.veiculo.toEntity() : null)
                .agregadoId(this.agregadoId)
                .build();
    }
}

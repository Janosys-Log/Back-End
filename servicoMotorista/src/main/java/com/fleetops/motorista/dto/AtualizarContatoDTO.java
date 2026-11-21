package com.fleetops.motorista.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarContatoDTO(
        @NotBlank(message = "O contato do motorista é obrigatório")
                @Size(max = 20, message = "O contato deve ter no máximo 20 caracteres")
                String contatoMotorista) {}

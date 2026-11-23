package com.fleetops.motorista.dto;

import com.fleetops.motorista.domain.TipoContato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarContatoDTO(
        @NotBlank(message = "O contato principal do motorista é obrigatório")
                @Size(max = 20, message = "O contato principal deve ter no máximo 20 caracteres")
                String contatoMotorista,
        TipoContato tipoContato,
        @Size(max = 20, message = "O contato secundário deve ter no máximo 20 caracteres") String contatoSecundario,
        TipoContato tipoContatoSecundario) {}

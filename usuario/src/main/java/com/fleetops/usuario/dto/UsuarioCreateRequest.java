package com.fleetops.usuario.dto;

import com.fleetops.usuario.domain.Papel;
import com.fleetops.usuario.domain.PerfilAcesso;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
        @NotBlank(message = "O nome é obrigatório")
                @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
                String nome,
        @NotBlank(message = "O e-mail é obrigatório")
                @Email(message = "Formato de e-mail inválido")
                @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
                String email,
        @NotBlank(message = "A senha é obrigatória")
                @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
                String senha,
        @NotBlank(message = "O usuário (login) é obrigatório")
                @Size(max = 50, message = "O usuário deve ter no máximo 50 caracteres")
                String login,
        @NotNull(message = "O perfil é obrigatório") PerfilAcesso perfil,
        @NotNull(message = "O papel é obrigatório") Papel papel) {}

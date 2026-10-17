package com.fleetops.usuario.dto;

import com.fleetops.usuario.domain.PerfilAcesso;

public record LoginResponseDTO(
        String token, String tipo, Long idUsuario, String nome, String email, PerfilAcesso perfil, Long expiraEm) {}

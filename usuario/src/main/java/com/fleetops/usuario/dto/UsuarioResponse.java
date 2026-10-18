package com.fleetops.usuario.dto;

import com.fleetops.usuario.domain.Papel;
import com.fleetops.usuario.domain.PerfilAcesso;
import com.fleetops.usuario.domain.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String login,
        PerfilAcesso perfil,
        Papel papel,
        boolean ativo,
        boolean primario) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
                u.getIdUsuario(),
                u.getNomeUsuario(),
                u.getEmailUsuario(),
                u.getLoginUsuario(),
                u.getPerfilAcesso(),
                u.getPapel(),
                Boolean.TRUE.equals(u.getAtivo()),
                Boolean.TRUE.equals(u.getPrimario()));
    }
}

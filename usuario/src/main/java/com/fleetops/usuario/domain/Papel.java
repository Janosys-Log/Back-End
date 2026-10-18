package com.fleetops.usuario.domain;

public enum Papel {
    GERENTE_OPERACIONAL(PerfilAcesso.GERENCIA),
    LIDER(PerfilAcesso.GERENCIA),
    OPERADOR_LOGISTICO(PerfilAcesso.OPERACIONAL),
    SAC(PerfilAcesso.OPERACIONAL);

    private final PerfilAcesso perfil;

    Papel(PerfilAcesso perfil) {
        this.perfil = perfil;
    }

    public boolean pertenceA(PerfilAcesso p) {
        return this.perfil == p;
    }
}

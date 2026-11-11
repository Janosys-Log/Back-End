package com.fleetops.motorista.exceptions;

public class ManifestoEmUsoException extends RuntimeException {
    public ManifestoEmUsoException(Long numeroManifesto) {
        super("O manifesto " + numeroManifesto + " já está em uso por outro motorista em rota");
    }
}

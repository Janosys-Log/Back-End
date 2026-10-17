package com.fleetops.motorista.exceptions;

public class MotoristaNaoEncontradoException extends RuntimeException {
    public MotoristaNaoEncontradoException(Long motoristaId) {
        super("Não é possível encontrar o motorista " + motoristaId);
    }
}

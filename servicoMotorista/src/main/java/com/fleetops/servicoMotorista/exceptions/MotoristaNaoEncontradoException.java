package com.fleetops.servicoMotorista.exceptions;

public class MotoristaNaoEncontradoException extends RuntimeException {
    public MotoristaNaoEncontradoException(Long motoristaId) {
        super("Não é possível encontrar o motorista " + motoristaId);
    }
}

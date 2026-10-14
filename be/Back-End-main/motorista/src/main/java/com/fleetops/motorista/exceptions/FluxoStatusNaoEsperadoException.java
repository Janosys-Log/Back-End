package com.fleetops.motorista.exceptions;

import com.fleetops.motorista.domain.StatusMotorista;

public class FluxoStatusNaoEsperadoException extends RuntimeException {
    public FluxoStatusNaoEsperadoException(StatusMotorista status) {
        super("Não é possível alterar diretamente o status atual para " + status);
    }
}

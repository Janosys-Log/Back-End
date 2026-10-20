package com.fleetops.servicoMotorista.exceptions;

import com.fleetops.servicoMotorista.domain.StatusMotorista;

public class FluxoStatusNaoEsperadoException extends RuntimeException {
    public FluxoStatusNaoEsperadoException(StatusMotorista status) {
        super("Não é possível alterar diretamente o status atual para " + status);
    }
}

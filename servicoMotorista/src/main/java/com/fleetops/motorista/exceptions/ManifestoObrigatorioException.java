package com.fleetops.motorista.exceptions;

public class ManifestoObrigatorioException extends RuntimeException {
    public ManifestoObrigatorioException() {
        super("O número do manifesto é obrigatório para colocar o motorista em rota");
    }
}

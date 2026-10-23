package com.fleetops.avaliacao.exception;

public class AvaliacaoNaoEncontrada extends RuntimeException {
    public String customMessagem;

    public AvaliacaoNaoEncontrada(Long id, String customMessage) {
        super("Não foi possivel encontrar a avaliação: " + id);
        this.customMessagem = customMessage;
    }
}

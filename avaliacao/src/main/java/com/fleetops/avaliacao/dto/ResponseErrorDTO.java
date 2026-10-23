package com.fleetops.avaliacao.dto;

import lombok.Getter;

@Getter
public class ResponseErrorDTO {
    private String titulo;
    private String mensagem;

    public ResponseErrorDTO(String titulo, String mensagem) {
        this.titulo = titulo;
        this.mensagem = mensagem;
    }
}

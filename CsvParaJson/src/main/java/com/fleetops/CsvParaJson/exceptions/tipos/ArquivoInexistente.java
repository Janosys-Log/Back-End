package com.fleetops.CsvParaJson.exceptions.tipos;

public class ArquivoInexistente extends RuntimeException {
     private String mensagem;

    public ArquivoInexistente(String titulo, String mensagem) {
        super(titulo);
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}

package com.fleetops.servicoMotorista.ouvinte.dto.response;

import java.util.List;

public class ErroLinhasDoCsv {
    private Long Linha;
    private List<String> mensagem;

    public ErroLinhasDoCsv(Long Linha, List<String> mensagem) {
        this.Linha = Linha;
        this.mensagem = mensagem;
    }

    public Long getLinha() {
        return Linha;
    }

    public void setLinha(Long linha) {
        Linha = linha;
    }

    public List<String> getMensagem() {
        return mensagem;
    }

    public void setMensagem(List<String> mensagem) {
        this.mensagem = mensagem;
    }
}

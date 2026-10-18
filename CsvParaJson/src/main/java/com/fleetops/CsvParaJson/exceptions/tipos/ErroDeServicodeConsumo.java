package com.fleetops.CsvParaJson.exceptions.tipos;

public class ErroDeServicodeConsumo extends RuntimeException {
    private String mensagemErro;
    public  ErroDeServicodeConsumo(String mensagemErro,String titulo){
        super(titulo);
      this.mensagemErro=mensagemErro;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }
    
}

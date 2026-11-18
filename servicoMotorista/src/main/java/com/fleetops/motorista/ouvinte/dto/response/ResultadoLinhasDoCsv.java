package com.fleetops.motorista.ouvinte.dto.response;

import java.util.List;

public class ResultadoLinhasDoCsv<T> {
    private List<T> validos;
    private List<ErroLinhasDoCsv> invalidos;
    private String nomeArquivo;
    private String dataHora;

    public ResultadoLinhasDoCsv() {}

    public ResultadoLinhasDoCsv(List<T> veiculos, List<ErroLinhasDoCsv> erros) {
        this.validos = veiculos;
        this.invalidos = erros;
    }

    public ResultadoLinhasDoCsv(List<T> veiculos, List<ErroLinhasDoCsv> erros, String nomeArquivo, String dataHora) {
        this.validos = veiculos;
        this.invalidos = erros;
        this.nomeArquivo = nomeArquivo;
        this.dataHora = dataHora;
    }

    public List<ErroLinhasDoCsv> getInvalidos() {
        return invalidos;
    }

    public void setInvalidos(List<ErroLinhasDoCsv> Invalidos) {
        this.invalidos = Invalidos;
    }

    public List<T> getValidos() {
        return validos;
    }

    public void setValidos(List<T> Validos) {
        this.validos = Validos;
    }

    public void addValidos(T veiculo) {
        this.validos.add(veiculo);
    }

    public void addErros(ErroLinhasDoCsv erro) {
        this.invalidos.add(erro);
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }
}

package com.fleetops.CsvParaJson.dto.response;

public class ResultadoCsv<T, K> {
    private ResultadoLinhasDoCsv<T> resultadoLinhasDoCsv;
    private ResultadoLinhasDoCsv<K> resultadoLinhasDoCsv2;

    public ResultadoCsv(ResultadoLinhasDoCsv<T> resultado1, ResultadoLinhasDoCsv<K> resultado2) {
        this.resultadoLinhasDoCsv = resultado1;
        this.resultadoLinhasDoCsv2 = resultado2;
    }

    public ResultadoLinhasDoCsv<T> getResultadoLinhasDoCsv() {
        return resultadoLinhasDoCsv;
    }

    public void setResultadoLinhasDoCsv(ResultadoLinhasDoCsv<T> resultadoLinhasDoCsv) {
        this.resultadoLinhasDoCsv = resultadoLinhasDoCsv;
    }

    public ResultadoLinhasDoCsv<K> getResultadoLinhasDoCsv2() {
        return resultadoLinhasDoCsv2;
    }

    public void setResultadoLinhasDoCsv2(ResultadoLinhasDoCsv<K> resultadoLinhasDoCsv2) {
        this.resultadoLinhasDoCsv2 = resultadoLinhasDoCsv2;
    }
}

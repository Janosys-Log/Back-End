package com.fleetops.motorista.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseDate {
    private LocalDate data;
    private String hora;
    private String dataHora;
    private String nomeArquivo;
    private String tipoArquivo;

    public ResponseDate() {}

    public ResponseDate(LocalDate data) {
        this.data = data;
    }

    public ResponseDate(LocalDateTime dtHora, String nomeArquivo, String tipoArquivo) {
        if (dtHora != null) {
            this.data = dtHora.toLocalDate();
            this.hora = dtHora.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            this.dataHora = dtHora.toString();
        }
        this.nomeArquivo = nomeArquivo;
        this.tipoArquivo = tipoArquivo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }
}

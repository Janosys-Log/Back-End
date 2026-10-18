package com.fleetops.CsvParaJson.dto.response;

import java.math.BigDecimal;

import com.univocity.parsers.annotations.Parsed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ManifestoDTO {

    @Parsed(field = "Manifesto (id)")
    @NotBlank(message = "A linha precisa ter um campo 'Manifesto (id)' preenchido")
    @Pattern (regexp = "^\\d+$",message = "Somente Numeros são permitidos no campo 'Manifesto (id)'")
    private String manifestoid;

    @Parsed (field = "Data")
    @NotBlank (message = "A linha precisa ter um campo 'Data' preenchido")
    @Pattern(regexp = "^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/\\d{4}$",message = "O campo 'Data' aceita datas no formato 'dd/mm/aaaa'")
    private String data;
    
    @Parsed (field = "Motorista")
    @NotBlank (message = "A linha precisa ter o campo 'Motorista' preenchido")
    @Pattern (regexp = "[\\p{L} ]+",message = "O campo 'Motorista' só podeter letras,espaços e acentos")
    private String motorista;

    @Parsed(field = "CPF (motorista)")
    @Pattern (regexp ="^\\d{3}\\.?\\d{3}\\.?\\d{3}\\d{2}$",message = "Formato de do campo 'CPF Motorista' inválido")
    private String cpfmotorista;

    @Parsed(field = "Agregado")
    @NotBlank(message = "A linha Precisa ter um campo 'Agragado' preenchido")
    @Pattern(regexp = "^[^0-9]*$", message = "O campo Agragado não pode conter números")
    private String agregado;

    @Parsed (field = "CNPJ (agregado)")
    @Pattern(regexp = "^[A-Z0-9]{12}\\d{2}$",message = "Formato do campo 'CNPJ (agregado)' inválido")
    private String cnpjagregado;

    @Parsed(field = "Veículo (placa)")
    @NotBlank(message = "A linha precisa ter um campo 'Placa' preenchido")
    @Pattern(regexp = "^[A-Z0-9]{7}$", message = "A placa deve ter exatamente 7 caracteres (letras e números, sem hífen, barra ou espaços)")
    private String placa;

    @Parsed (field = "Destino")
    @NotBlank(message = "A linha precisa ter um campo 'Destino' preenchido")
    @Pattern(regexp="[\\p{L} ]+",message = "O campo 'Destino' só aceita letras(com  acentos) e espaços")
    private String destino;

    @Parsed (field = "Valor Frete(agregado)")
    @NotBlank (message = "A linha precisa ter um campo 'Valor Frete(agregado)'")
    @Pattern (regexp = "\\d{1,3}(\\.\\d{3})*,\\d{2}",message = "O campo 'Valor Frete(agregado)' só aceita o formato da moeda brasileira")
    private String valorfreteagregado;

    @Parsed (field = "Valor Frete(empresa)")
    @NotBlank (message = "A linha precisa ter um campo 'Valor Frete(empresa)'")
    @Pattern (regexp = "\\d{1,3}(\\.\\d{3})*,\\d{2}",message = "O campo 'Valor Frete(empresa)' só aceita o formato da moeda brasileira")
    private String valorfreteempresa;
   
    @Parsed (field = "Observações")
    private String observacoes;

    public String getAgregado() {
        return agregado;
    }

    public void setAgregado(String agregado) {
        this.agregado = agregado;
    }

    public String getCnpjagregado() {
        return cnpjagregado;
    }

    public void setCnpjagregado(String cnpjagregado) {
        this.cnpjagregado = cnpjagregado;
    }

    public String getCpfmotorista() {
        return cpfmotorista;
    }

    public void setCpfmotorista(String cpfmotorista) {
        this.cpfmotorista = cpfmotorista;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Long getManifestoid() {
        if (manifestoid == null || !manifestoid.matches("\\d+")) {
            return null;
        }
        return Long.parseLong(manifestoid);
    }

    public void setManifestoid(String manifestoid) {
        this.manifestoid = manifestoid;
    }

    public String getMotorista() {
        return motorista;
    }

    public void setMotorista(String motorista) {
        this.motorista = motorista;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public BigDecimal getValorfreteagregado() {
    if (valorfreteagregado == null || valorfreteagregado.isBlank()) return null;
    String s = valorfreteagregado.trim();
    s = s.matches(".*,\\d{1,2}$") ? s.replace(".", "").replace(",", ".") : s.replace(",", "");
    try { return new BigDecimal(s); } catch (NumberFormatException e) { return null; }
}

    public void setValorfreteagregado(String valorfreteagregado) {
        this.valorfreteagregado = valorfreteagregado;
    }

      public BigDecimal getValorfreteempresa() {
    if (valorfreteempresa == null || valorfreteempresa.isBlank()) return null;
    String s = valorfreteempresa.trim();
    s = s.matches(".*,\\d{1,2}$") ? s.replace(".", "").replace(",", ".") : s.replace(",", "");
    try { return new BigDecimal(s); } catch (NumberFormatException e) { return null; }
}

    public void setValorfreteempresa(String valorfreteempresa) {
        this.valorfreteempresa = valorfreteempresa;
    }
}
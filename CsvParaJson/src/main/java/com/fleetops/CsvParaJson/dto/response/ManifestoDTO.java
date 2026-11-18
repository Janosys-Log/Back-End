package com.fleetops.CsvParaJson.dto.response;

import com.fleetops.CsvParaJson.converters.AgregadoCleanerConversion;
import com.fleetops.CsvParaJson.converters.DigitsOnlyConversion;
import com.fleetops.CsvParaJson.converters.PlacaCleanerConversion;
import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class ManifestoDTO {

    @Parsed(field = {"Manifesto", "Manifesto (id)"})
    @Convert(conversionClass = DigitsOnlyConversion.class)
    @NotBlank(message = "A linha precisa ter um campo 'Manifesto (id)' preenchido")
    @Pattern(regexp = "^\\d+$", message = "Somente Numeros são permitidos no campo 'Manifesto (id)'")
    private String manifestoid;

    @Parsed(field = "Data")
    @NotBlank(message = "A linha precisa ter um campo 'Data' preenchido")
    @Pattern(
            regexp = "^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/\\d{4}$",
            message = "O campo 'Data' aceita datas no formato 'dd/mm/aaaa'")
    private String data;

    @Parsed(field = "Motorista")
    @NotBlank(message = "A linha precisa ter o campo 'Motorista' preenchido")
    @Pattern(regexp = "[\\p{L} ]+", message = "O campo 'Motorista' só podeter letras,espaços e acentos")
    private String motorista;

    @Parsed(field = {"CPF", "CPF (motorista)"})
    @Convert(conversionClass = DigitsOnlyConversion.class)
    @NotBlank(message = "A linha precisa ter o campo 'CPF Motorista' preenchido")
    @Pattern(regexp = "^\\d{11}$", message = "Formato de do campo 'CPF Motorista' inválido")
    private String cpfmotorista;

    @Parsed(field = "Agregado")
    @Convert(conversionClass = AgregadoCleanerConversion.class)
    @NotBlank(message = "A linha Precisa ter um campo 'Agragado' preenchido")
    private String agregado;

    @Parsed(field = {"CPF/CNPJ Agregado", "CNPJ (agregado)", "CNPJ Agregado"})
    @Convert(conversionClass = DigitsOnlyConversion.class)
    @Pattern(regexp = "^[A-Z0-9]{11,14}$", message = "Formato do campo 'CNPJ (agregado)' inválido")
    private String cnpjagregado;

    @Parsed(field = {"Veículo", "Veículo (placa)", "Veiculo"})
    @Convert(conversionClass = PlacaCleanerConversion.class)
    @NotBlank(message = "A linha precisa ter um campo 'Placa' preenchido")
    @Pattern(
            regexp = "^[A-Z0-9]{7}$",
            message = "A placa deve ter exatamente 7 caracteres (letras e números, sem hífen, barra ou espaços)")
    private String placa;

    @Parsed(field = "Destino")
    @Convert(conversionClass = AgregadoCleanerConversion.class)
    @NotBlank(message = "A linha precisa ter um campo 'Destino' preenchido")
    private String destino;

    @Parsed(field = {"Valor Frete", "Valor Frete(agregado)"})
    @NotBlank(message = "A linha precisa ter um campo 'Valor Frete(agregado)'")
    @Pattern(
            regexp = "^\\d{1,3}(\\.\\d{3})*(,\\d{1,2})?$|^\\d+(,\\d{1,2})?$",
            message = "O campo 'Valor Frete(agregado)' só aceita o formato da moeda brasileira")
    private String valorfreteagregado;

    @Parsed(field = {"Valor Fretes", "Valor Frete(empresa)"})
    @NotBlank(message = "A linha precisa ter um campo 'Valor Frete(empresa)'")
    @Pattern(
            regexp = "^\\d{1,3}(\\.\\d{3})*(,\\d{1,2})?$|^\\d+(,\\d{1,2})?$",
            message = "O campo 'Valor Frete(empresa)' só aceita o formato da moeda brasileira")
    private String valorfreteempresa;

    @Parsed(field = {"Observações operacionais", "Observações", "Observacoes operacionais", "Observacoes"})
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
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setValorfreteagregado(String valorfreteagregado) {
        this.valorfreteagregado = valorfreteagregado;
    }

    public BigDecimal getValorfreteempresa() {
        if (valorfreteempresa == null || valorfreteempresa.isBlank()) return null;
        String s = valorfreteempresa.trim();
        s = s.matches(".*,\\d{1,2}$") ? s.replace(".", "").replace(",", ".") : s.replace(",", "");
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setValorfreteempresa(String valorfreteempresa) {
        this.valorfreteempresa = valorfreteempresa;
    }
}

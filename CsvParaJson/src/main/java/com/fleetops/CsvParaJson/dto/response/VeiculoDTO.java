package com.fleetops.CsvParaJson.dto.response;

import com.fleetops.CsvParaJson.converters.AgregadoCleanerConversion;
import com.fleetops.CsvParaJson.converters.DigitsOnlyConversion;
import com.fleetops.CsvParaJson.converters.MotoristaCleanerConversion;
import com.fleetops.CsvParaJson.converters.PlacaCleanerConversion;
import com.fleetops.CsvParaJson.converters.SubtipoVeiculoConversion;
import com.fleetops.CsvParaJson.enums.SubtipoVeiculo;
import com.fleetops.CsvParaJson.enums.TipoVeiculo;
import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.conversions.EnumSelector;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VeiculoDTO {

    @Parsed(field = {"Placa", "Placa (id)"})
    @Convert(conversionClass = PlacaCleanerConversion.class)
    @NotBlank(message = "A linha precisa ter um campo 'Placa' preenchido")
    @Pattern(
            regexp = "^[A-Z0-9]{7}$",
            message = "A placa deve ter exatamente 7 caracteres (letras e números, sem hífen, barra ou espaços)")
    private String placa;

    @Parsed(field = {"Agregado", "Proprietário"})
    @Convert(conversionClass = AgregadoCleanerConversion.class)
    @NotBlank(message = "A linha Precisa ter um campo 'Agragado' preenchido")
    private String agregado;

    @Parsed(field = {"Status do Veículo", "Status"})
    @NotBlank(message = "A linha Precisa ter um campo 'Status Do Veículo' preenchido")
    @Pattern(regexp = "Ativo|Inativo", message = "'Status Do Veículo' deve ser Ativo ou Inativo")
    private String status;

    @Parsed(field = {"Motorista Padrão", "Motorista"})
    @Convert(conversionClass = MotoristaCleanerConversion.class)
    @NotBlank(message = "A linha Precisa ter um campo 'Motorista Padrão' preenchido")
    private String motorista;

    private TipoVeiculo tipoVeiculo;

    @Parsed(field = {"Subtipo Veículo", "Subtipo Veiculo", "Tipo Veículo", "Tipo Veiculo"})
    @Convert(conversionClass = SubtipoVeiculoConversion.class)
    @EnumOptions(selectors = {EnumSelector.NAME})
    @NotNull(message = "A linha precisa ter um campo 'Subtipo Veículo'")
    private SubtipoVeiculo subtipoVeiculo;

    @Parsed(field = {"CPF Motorista", "CPF"})
    @Convert(conversionClass = DigitsOnlyConversion.class)
    @Pattern(regexp = "^\\d{11}$", message = "Formato de do campo 'CPF Motorista' inválido")
    private String cpfmotorista;

    @Parsed(field = {"CNPJ Agregado", "CNPJ (agregado)", "CPF/CNPJ Agregado"})
    @Convert(conversionClass = DigitsOnlyConversion.class)
    @Pattern(regexp = "^[A-Z0-9]{11,14}$", message = "Formato do campo 'CNPJ Agregado' inválido")
    private String cnpjagregado;

    @Parsed(field = {"Contato Motorista", "Contato"})
    @Convert(conversionClass = DigitsOnlyConversion.class)
    @Pattern(
            regexp = "^\\d{10,11}$",
            message = "O campo 'Contato Motorista' deve conter apenas números (10 ou 11 dígitos)")
    private String contatomotorista;

    @Parsed(field = {"Contato Agregado"})
    @Convert(conversionClass = DigitsOnlyConversion.class)
    @Pattern(
            regexp = "^\\d{10,11}$",
            message = "O campo  'Contato do Agregado' deve conter apenas números (10 ou 11 dígitos)")
    private String contatoagregado;

    public VeiculoDTO(
            String Placa,
            String Agregado,
            String Status,
            String Motorista,
            TipoVeiculo TipoVeiculo,
            SubtipoVeiculo Subtipo,
            String cppfMotorista,
            String cppjAgregado,
            String contatoMotorista,
            String contatoAgregado) {
        this.agregado = Agregado;
        this.motorista = Motorista;
        this.placa = Placa;
        this.status = Status;
        this.tipoVeiculo = TipoVeiculo;
        this.subtipoVeiculo = Subtipo;
        this.cnpjagregado = cppjAgregado;
        this.cpfmotorista = cppfMotorista;
        this.contatomotorista = contatoMotorista;
        this.contatoagregado = contatoAgregado;
    }

    public VeiculoDTO() {}

    public String getAgregado() {
        return agregado;
    }

    public void setAgregado(String Agregado) {
        agregado = Agregado;
    }

    public String getMotorista() {
        return motorista;
    }

    public void setMotorista(String Motorista) {
        motorista = Motorista;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String Placa) {
        placa = Placa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String Status) {
        status = Status;
    }

    @NotNull(message = "A linha Precisa ter um campo 'Tipo Veículo' preenchido")
    public TipoVeiculo getTipoVeiculo() {
        if (tipoVeiculo != null) {
            return tipoVeiculo;
        }
        if (subtipoVeiculo != null) {
            return subtipoVeiculo.getTipo();
        }
        return null;
    }

    public void setTipoVeiculo(TipoVeiculo TipoVeiculo) {
        tipoVeiculo = TipoVeiculo;
    }

    public SubtipoVeiculo getSubtipoVeiculo() {
        return subtipoVeiculo;
    }

    public void setSubtipoVeiculo(SubtipoVeiculo subtipoVeiculo) {
        this.subtipoVeiculo = subtipoVeiculo;
        if (subtipoVeiculo != null) {
            this.tipoVeiculo = subtipoVeiculo.getTipo();
        }
    }

    public String getCnpjagregado() {
        return cnpjagregado;
    }

    public void setCnpjagregado(String cnpjagregado) {
        this.cnpjagregado = cnpjagregado;
    }

    public String getContatoagregado() {
        return contatoagregado;
    }

    public void setContatoagregado(String contatoagregado) {
        this.contatoagregado = contatoagregado;
    }

    public String getContatomotorista() {
        return contatomotorista;
    }

    public void setContatomotorista(String contatomotorista) {
        this.contatomotorista = contatomotorista;
    }

    public String getCpfmotorista() {
        return cpfmotorista;
    }

    public void setCpfmotorista(String cpfmotorista) {
        this.cpfmotorista = cpfmotorista;
    }
}

package com.fleetops.CsvParaJson.dto.response;

import com.fleetops.CsvParaJson.enums.SubtipoVeiculo;
import com.fleetops.CsvParaJson.enums.TipoVeiculo;
import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.conversions.EnumSelector;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
public class VeiculoDTO {
    @Parsed(field = "Placa (id)")
    @NotBlank(message = "O Veiculo Precisa ter uma Placa")
    @Pattern(regexp = "^[A-Z0-9]{7}$", message = "A placa deve ter exatamente 7 caracteres (letras e números, sem hífen, barra ou espaços)")
    private String placa;

    @Parsed(field = "Agregado")
    @NotBlank(message = "O Veiculo Precisa ter um  Agragado")
    @Pattern(regexp = "^[^0-9]*$", message = "O campo Agragado não pode conter números")
    private String agregado;

    @Parsed(field = "Status do Veículo")
    @NotBlank(message = "O Veiculo Precisa ter um Status")
    @Pattern(regexp = "Ativo|Inativo", message = "Status deve ser Ativo ou Inativo")
    private String status;

    @Parsed(field = "Motorista Padrão")
    @NotBlank(message = "O Veiculo Precisa ter um Motorista")
    @Pattern(regexp = "^[^0-9]*$", message = "O campo Motorista não pode conter números")
    private String motorista;

    @Parsed(field = "Tipo Veículo")
    @NotNull(message = "O Veiculo Precisa ter um Tipo")
    @EnumOptions(customElement = "descricao",selectors = {EnumSelector.CUSTOM_FIELD})
    private TipoVeiculo tipoVeiculo;

    @Parsed(field = "Subtipo Veiculo")
    @NotNull (message = "O Veiculo precisa ter Subtipo")
    @EnumOptions(customElement = "descricao",selectors = {EnumSelector.CUSTOM_FIELD})
     private SubtipoVeiculo subtipoVeiculo;

    @Parsed(field = "CPF Motorista")
    @Pattern (regexp ="^\\d{3}\\.?\\d{3}\\.?\\d{3}\\d{2}$",message = "Formato de CPF inaválido")
    private String cpfmotorista;

    @Parsed (field = "CNPJ Agregado")
    @Pattern(regexp = "^[A-Z0-9]{12}\\d{2}$",message = "Formato de CNPJ inválido")
     private String cnpjagregado;
    
     @Parsed (field = "Contato Motorista")
    @Pattern(regexp = "^\\d{10,11}$", message = "O Contato do Motorista deve conter apenas números (10 ou 11 dígitos)")
     private String contatomotorista;
    
     @Parsed (field = "Contato Agregado")
     @Pattern(regexp = "^\\d{10,11}$", message = "O Contato do Agregado deve conter apenas números (10 ou 11 dígitos)")
     private String contatoagregado;
    
    


    public VeiculoDTO(String Placa, String Agregado, String Status, String Motorista, TipoVeiculo TipoVeiculo,SubtipoVeiculo Subtipo,String cppfMotorista,String cppjAgregado,String contatoMotorista,String contatoAgregado) {
        this.agregado = Agregado;
        this.motorista = Motorista;
        this.placa = Placa;
        this.status = Status;
        this.tipoVeiculo = TipoVeiculo;
        this.subtipoVeiculo=Subtipo;
        this.cnpjagregado=cppjAgregado;
        this.cpfmotorista=cppfMotorista;
        this.contatomotorista=contatoMotorista;
        this.contatoagregado=contatoAgregado;

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

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculo TipoVeiculo) {
        tipoVeiculo = TipoVeiculo;
    }

    public SubtipoVeiculo getSubtipoVeiculo() {
        return subtipoVeiculo;
    }

    public void setSubtipoVeiculo(SubtipoVeiculo subtipoVeiculo) {
        this.subtipoVeiculo = subtipoVeiculo;
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

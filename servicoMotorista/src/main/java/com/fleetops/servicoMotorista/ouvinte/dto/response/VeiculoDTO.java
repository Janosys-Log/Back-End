package com.fleetops.servicoMotorista.ouvinte.dto.response;

import com.fleetops.servicoMotorista.domain.SubtipoVeiculo;
import com.fleetops.servicoMotorista.domain.TipoVeiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VeiculoDTO {

    @NotBlank(message = "O Veiculo Precisa ter uma Placa")
    @NotNull(message = "Placa Precisa ter Valor")
    private String placa;

    @NotBlank(message = "O Veiculo Precisa ter um  Agragado")
    @NotNull(message = "Agragado Precisa ter valor")
    @Pattern(regexp = "^[^0-9]*$", message = "O campo Agragado não pode conter números")
    private String agregado;

    @NotBlank(message = "O Veiculo Precisa ter um Status")
    @NotNull(message = "Status precisa ter valor")
    @Pattern(regexp = "Ativo|Inativo", message = "Status deve ser Ativo ou Inativo")
    private String status;

    @NotBlank(message = "O Veiculo Precisa ter um Motorista")
    @NotNull(message = "O Motorista precisa ter valor")
    @Pattern(regexp = "^[^0-9]*$", message = "O campo Motorista não pode conter números")
    private String motorista;

    @NotBlank(message = "O Veiculo Precisa ter um Tipo")
    @NotNull(message = "TipoVeiculo Precisa ter valor")
    @Pattern(regexp = "^[^0-9]*$", message = "O campo TipoVeiculo não pode conter números")
    private TipoVeiculo tipoVeiculo;

    private SubtipoVeiculo subtipoVeiculo;

    private String cpfmotorista;

    private String cnpjagregado;

    private String contatomotorista;

    private String contatoagregado;

    public VeiculoDTO(String placa, String agregado, String Status, String Motorista, TipoVeiculo TipoVeiculo) {
        this.agregado = agregado;
        this.motorista = Motorista;
        this.placa = placa;
        this.status = Status;
        this.tipoVeiculo = TipoVeiculo;
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

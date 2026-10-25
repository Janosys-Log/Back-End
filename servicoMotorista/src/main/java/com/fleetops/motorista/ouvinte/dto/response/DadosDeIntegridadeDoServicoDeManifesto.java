package com.fleetops.motorista.ouvinte.dto.response;

public class DadosDeIntegridadeDoServicoDeManifesto<T> {
    private T cpfMotorista;
    private T cnpjAgregado;
    private T placaVeiculo;

    public DadosDeIntegridadeDoServicoDeManifesto(T cpf, T cnpj, T placa) {
        this.cnpjAgregado = cnpj;
        this.cpfMotorista = cpf;
        this.placaVeiculo = placa;
    }

    public T getCnpjAgregado() {
        return cnpjAgregado;
    }

    public void setCnpjAgregado(T cnpjAgregado) {
        this.cnpjAgregado = cnpjAgregado;
    }

    public T getCpfMotorista() {
        return cpfMotorista;
    }

    public void setCpfMotorista(T cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    public T getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(T placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }
}

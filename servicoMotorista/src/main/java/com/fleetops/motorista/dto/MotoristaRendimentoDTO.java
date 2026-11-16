package com.fleetops.motorista.dto;

public class MotoristaRendimentoDTO {
    private MotoristaDTO motorista;
    private RentabilidadeDTO rentabilidade;

    public MotoristaRendimentoDTO(MotoristaDTO moto, RentabilidadeDTO renta) {
        this.motorista = moto;
        this.rentabilidade = renta;
    }

    public MotoristaDTO getMotorista() {
        return motorista;
    }

    public void setMotorista(MotoristaDTO motorista) {
        this.motorista = motorista;
    }

    public RentabilidadeDTO getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(RentabilidadeDTO rentabilidade) {
        this.rentabilidade = rentabilidade;
    }
}

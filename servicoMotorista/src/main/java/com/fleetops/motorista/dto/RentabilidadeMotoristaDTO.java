package com.fleetops.motorista.dto;

import java.math.BigDecimal;

public class RentabilidadeMotoristaDTO {
    private Long mes;
    private Long ano;
    private BigDecimal rentabilidadeMedia ;
    RentabilidadeMotoristaDTO(Long mes,Long ano,BigDecimal rentabilidade){
        this.mes=mes;
        this.ano=ano;
        this.rentabilidadeMedia=rentabilidade;
    }

    public Long getAno() {
        return ano;
    }


    public void setAno(Long ano) {
        this.ano = ano;
    }

    public Long getMes() {
        return mes;
    }

    public void setMes(Long mes) {
        this.mes = mes;
    }

    public BigDecimal getRentabilidadeMedia() {
        return rentabilidadeMedia;
    }

    public void setRentabilidadeMedia(BigDecimal rentabilidadeMedia) {
        this.rentabilidadeMedia = rentabilidadeMedia;
    }
}

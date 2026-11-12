package com.fleetops.motorista.dto;

import java.math.BigDecimal;

public class RentabilidadeDTO {
    private Long mes;
    private Long ano;
    private BigDecimal rentabilidadeMedia;
    private BigDecimal crescimentoComparadoComOMesPassado;
    private Long numeroDeManifestos;
    private BigDecimal rendimentoTotal;

    public RentabilidadeDTO(
            Long mes,
            Long ano,
            BigDecimal rentabilidade,
            BigDecimal crescimentoEmPorcentagem,
            Long numeroDeManifestos,
            BigDecimal rendimentoTotal) {
        this.mes = mes;
        this.ano = ano;
        this.rentabilidadeMedia = rentabilidade;
        this.crescimentoComparadoComOMesPassado = crescimentoEmPorcentagem;
        this.numeroDeManifestos = numeroDeManifestos;
        this.rendimentoTotal = rendimentoTotal;
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

    public BigDecimal getCrescimentoComparadoComOMesPassado() {
        return crescimentoComparadoComOMesPassado;
    }

    public void setCrescimentoComparadoComOMesPassado(BigDecimal crescimentoCompradoComOMesPassado) {
        this.crescimentoComparadoComOMesPassado = crescimentoCompradoComOMesPassado;
    }

    public Long getNumeroDeManifestos() {
        return numeroDeManifestos;
    }

    public void setNumeroDeManifestos(Long numeroDeManifestos) {
        this.numeroDeManifestos = numeroDeManifestos;
    }

    public BigDecimal getRendimentoTotal() {
        return rendimentoTotal;
    }

    public void setRendimentoTotal(BigDecimal rendimentoTotal) {
        this.rendimentoTotal = rendimentoTotal;
    }
}

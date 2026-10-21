package com.fleetops.motorista.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.domain.Manifesto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ManifestoDTO(

        Long idManifesto,

        LocalDate dataManifesto,

        @Size(max = 100, message = "O destino deve ter no máximo 100 caracteres")
        String destino,

        LocalDate dataInsert,

        BigDecimal valorRecebido,

        BigDecimal freteEmpresa,

        BigDecimal freteAgregado,

        String observacoes,

        VeiculoDTO veiculo,

        Agregado agregado,

        @NotNull(message = "O motorista condutor é obrigatório")
        MotoristaDTO motorista
) {

    public static ManifestoDTO fromEntity(Manifesto manifesto) {
        return new ManifestoDTO(
                manifesto.getIdManifesto(),
                manifesto.getDataManifesto(),
                manifesto.getDestino(),
                manifesto.getDataInsert(),
                manifesto.getValorRecebido(),
                manifesto.getFreteEmpresa(),
                manifesto.getFreteAgregado(),
                manifesto.getObservacoes(),
                manifesto.getVeiculo() != null
                        ? VeiculoDTO.fromEntity(manifesto.getVeiculo())
                        : null,
                manifesto.getAgregado(),
                manifesto.getMotorista() != null
                        ? MotoristaDTO.fromEntity(manifesto.getMotorista())
                        : null
        );
    }

    public Manifesto toEntity() {
        return Manifesto.builder()
                .idManifesto(this.idManifesto)
                .dataManifesto(this.dataManifesto)
                .destino(this.destino)
                .dataInsert(this.dataInsert)
                .valorRecebido(this.valorRecebido)
                .freteEmpresa(this.freteEmpresa)
                .freteAgregado(this.freteAgregado)
                .observacoes(this.observacoes)
                .veiculo(this.veiculo != null ? this.veiculo.toEntity() : null)
                .agregado(this.agregado)
                .motorista(this.motorista != null ? this.motorista.toEntity() : null)
                .build();
    }
}
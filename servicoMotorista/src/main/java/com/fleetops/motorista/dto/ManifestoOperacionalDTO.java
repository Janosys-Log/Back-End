package com.fleetops.motorista.dto;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.domain.Manifesto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Resposta de manifesto para o perfil Operacional, sem a média de avaliação do motorista. */
public record ManifestoOperacionalDTO(
        Long idManifesto,
        LocalDate dataManifesto,
        @Size(max = 100, message = "O destino deve ter no máximo 100 caracteres") String destino,
        LocalDate dataInsert,
        BigDecimal valorRecebido,
        BigDecimal freteEmpresa,
        BigDecimal freteAgregado,
        String observacoes,
        VeiculoDTO veiculo,
        AgregadoOperacionalDTO agregado,
        @NotNull(message = "O motorista condutor é obrigatório") MotoristaOperacionalDTO motorista) {

    public static ManifestoOperacionalDTO fromEntity(Manifesto manifesto) {
        return new ManifestoOperacionalDTO(
                manifesto.getIdManifesto(),
                manifesto.getDataManifesto(),
                manifesto.getDestino(),
                manifesto.getDataInsert(),
                manifesto.getValorRecebido(),
                manifesto.getFreteEmpresa(),
                manifesto.getFreteAgregado(),
                manifesto.getObservacoes(),
                manifesto.getVeiculo() != null ? VeiculoDTO.fromEntity(manifesto.getVeiculo()) : null,
                manifesto.getAgregado() != null ? AgregadoOperacionalDTO.fromEntity(manifesto.getAgregado()) : null,
                manifesto.getMotorista() != null
                        ? MotoristaOperacionalDTO.fromEntity(manifesto.getMotorista())
                        : null);
    }
}

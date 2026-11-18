package com.fleetops.motorista.dto;

import com.fleetops.motorista.domain.StatusMotorista;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * DTO de entrada para atualização do status do motorista.
 * Request esperado (SCRUM-46):
 * - motoristaId: ID do motorista
 * - status: Disponivel, Em_Rota, Indisponivel, Indesejado
 * - numeroManifesto: obrigatório somente quando o status é Em_Rota (US12)
 */
@Getter
public class StatusDTO {
    @NotNull(message = "O ID do motorista é obrigatório")
    @Schema(
            description = "ID do motorista vinculado à avaliação",
            example = "42",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long motoristaId;

    @NotNull(message = "O Status é obrigatório")
    @Schema(
            description = "Status do Motorista (Disponivel, Em_Rota, Indisponivel, Indesejado)",
            example = "Disponivel",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private StatusMotorista status;

    @Min(value = 1, message = "O número do manifesto deve ser maior que zero")
    @Schema(
            description = "Número do manifesto da rota. Obrigatório quando o status é Em_Rota; ignorado nos demais",
            example = "123456",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long numeroManifesto;
}

package com.fleetops.servicoMotorista.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "motorista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_motorista")
    private Long idMotorista;

    @NotBlank(message = "O nome do motorista é obrigatório")
    @Size(max = 100, message = "O nome do motorista deve ter no máximo 100 caracteres")
    @Column(name = "nome_motorista", length = 100, nullable = false)
    private String nomeMotorista;

    @NotBlank(message = "O CPF do motorista é obrigatório")
    @Size(max = 11, message = "O CPF deve ter no máximo 11 dígitos numéricos")
    @Column(name = "cpf_motorista", length = 11, unique = true, nullable = false)
    private String cpfMotorista;

    @Size(max = 20, message = "O contato deve ter no máximo 20 caracteres")
    @Column(name = "contato_motorista", length = 20, nullable = false)
    private String contatoMotorista;

    /**
     * Status atual do motorista (obrigatório).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private StatusMotorista status = StatusMotorista.Disponivel;

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @Column(name = "contador_rota_sp")
    @Builder.Default
    private Integer contadorRotaSp = 0;

    @Column(name = "nota_media")
    @Builder.Default
    private Float notaMedia = 0.0f;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "veiculo_id", unique = true)
    private Veiculo veiculo;

    /**
     * ID do agregado ao qual o motorista está vinculado.
     * Mantido como Long (sem @OneToOne) para baixo acoplamento
     * e facilitar migração para microsserviço.
     */
    @NotNull(message = "O agregado ao qual o motorista pertence é obrigatório")
    @Column(name = "agregado_id")
    private Long agregadoId;

    public void atualizaStatus(StatusMotorista status) {
        this.status = status;
        this.statusUpdatedAt = LocalDateTime.now();
    }
}

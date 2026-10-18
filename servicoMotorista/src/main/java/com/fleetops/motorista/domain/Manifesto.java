package com.fleetops.motorista.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "manifesto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manifesto {

    @Id
    @Column(name = "id_manifesto")
    private Long idManifesto;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "data_manifesto")
    private LocalDate dataManifesto;

    @Size(max = 100, message = "O destino deve ter no máximo 100 caracteres")
    @Column(name = "destino", length = 100)
    private String destino;

    @Column(name = "valor_recebido", precision = 12, scale = 2)
    private BigDecimal valorRecebido;

    @Column(name = "freteEmp", precision = 12, scale = 2)
    private BigDecimal freteEmpresa;

    @Column(name = "freteAgregado", precision = 12, scale = 2)
    private BigDecimal freteAgregado;

    @Column(name = "observacoes")
    private String observacoes;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "veiculo_id",nullable = false)
    private Veiculo veiculo;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "agregado_id",nullable = false)
    private  Agregado agregado;

    @NotNull(message = "O motorista condutor é obrigatório")
    @ManyToOne  (fetch = FetchType.EAGER)
    @JoinColumn (name = "motorista_id",nullable = false)
    private Motorista motorista;

}

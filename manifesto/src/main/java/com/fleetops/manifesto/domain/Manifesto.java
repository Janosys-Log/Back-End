package com.fleetops.manifesto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_manifesto")
    private Long idManifesto;

    @Column(name = "data_manifesto")
    private LocalDate dataManifesto;

    @Size(max = 100, message = "O destino deve ter no máximo 100 caracteres")
    @Column(name = "destino", length = 100)
    private String destino;

    @Column(name = "valor_recebido", precision = 12, scale = 2)
    private BigDecimal valorRecebido;

    @Column(name = "frete", precision = 12, scale = 2)
    private BigDecimal frete;

    @Column(name = "aereo")
    private Boolean aereo;

    @NotNull(message = "O motorista condutor é obrigatório")
    @Column(name = "motorista_id", nullable = false, unique = true)
    private Long motoristaId;

    @NotNull(message = "O agregado parceiro é obrigatório")
    @Column(name = "agregado_id", nullable = false, unique = true)
    private Long agregadoId;

    @NotNull(message = "O veículo utilizado é obrigatório")
    @Column(name = "veiculo_id", nullable = false, unique = true)
    private Long veiculoId;
}

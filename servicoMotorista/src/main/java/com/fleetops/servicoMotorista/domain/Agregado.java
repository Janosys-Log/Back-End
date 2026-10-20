package com.fleetops.servicoMotorista.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "agregado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agregado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agregado")
    private Long idAgregado;

    @NotBlank(message = "O nome do agregado é obrigatório")
    @Size(max = 150, message = "O nome do agregado deve ter no máximo 150 caracteres")
    @Column(name = "nome_agregado", length = 150, nullable = false)
    private String nomeAgregado;

    @NotBlank(message = "O CNPJ do agregado é obrigatório")
    @Size(max = 14, message = "O CNPJ deve ter no máximo 14 dígitos numéricos")
    @Column(name = "cnpj_agregado", length = 14, unique = true, nullable = false)
    private String cnpjAgregado;

    @Size(max = 20, message = "O contato deve ter no máximo 20 caracteres")
    @Column(name = "contato_agregado", length = 20)
    private String contatoAgregado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "agregado_id")
    @Builder.Default
    private Set<Motorista> motoristas = new HashSet<Motorista>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "agregado_id", insertable = false, updatable = false)
    @Builder.Default
    private Set<Veiculo> veiculos = new HashSet<Veiculo>();
}

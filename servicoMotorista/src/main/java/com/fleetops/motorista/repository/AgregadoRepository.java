package com.fleetops.motorista.repository;

import com.fleetops.motorista.domain.Agregado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgregadoRepository extends JpaRepository<Agregado, Long> {
    Agregado findBynomeAgregado(String nome);

    Agregado findBycnpjAgregado(String cnpj);

    boolean existsBycnpjAgregado(String cnpf);
}

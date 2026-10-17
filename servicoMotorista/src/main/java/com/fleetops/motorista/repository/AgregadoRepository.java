package com.fleetops.motorista.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fleetops.motorista.domain.Agregado;

public interface AgregadoRepository extends JpaRepository<Agregado, Long> {
    Agregado findBynomeAgregado(String nome);

    Agregado findBycnpjAgregado(String cnpj);

    boolean existsBycnpjAgregado(String cnpf);
}

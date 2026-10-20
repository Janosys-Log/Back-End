package com.fleetops.servicoMotorista.repository;

import com.fleetops.servicoMotorista.domain.Agregado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgregadoRepository extends JpaRepository<Agregado, Long> {
    Agregado findBynomeAgregado(String nome);

    Agregado findBycnpjAgregado(String cnpj);

    boolean existsBycnpjAgregado(String cnpf);
}

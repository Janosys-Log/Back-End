package com.fleetops.servicoMotorista.repository;

import com.fleetops.servicoMotorista.domain.Manifesto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManifestoRepositorio extends JpaRepository<Manifesto, Long> {
    Optional<Manifesto> findFirstByOrderByDataInsert();
}

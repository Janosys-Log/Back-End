package com.fleetops.motorista.repository;

import com.fleetops.motorista.domain.Manifesto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManifestoRepositorio extends JpaRepository<Manifesto, Long> {
    Optional<Manifesto> findFirstByOrderByDataInsertDesc();
}

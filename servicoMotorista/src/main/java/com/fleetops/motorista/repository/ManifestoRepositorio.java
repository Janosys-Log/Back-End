package com.fleetops.motorista.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fleetops.motorista.domain.Manifesto;

public interface ManifestoRepositorio extends JpaRepository<Manifesto, Long> {
    Optional<Manifesto> findFirstByOrderByDataInsertDesc();
}

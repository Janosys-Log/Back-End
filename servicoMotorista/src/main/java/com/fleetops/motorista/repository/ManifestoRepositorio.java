package com.fleetops.motorista.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fleetops.motorista.domain.Manifesto;

public interface ManifestoRepositorio extends JpaRepository<Manifesto, Long> {}

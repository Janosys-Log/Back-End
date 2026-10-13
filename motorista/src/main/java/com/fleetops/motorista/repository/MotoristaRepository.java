package com.fleetops.motorista.repository;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotoristaRepository extends JpaRepository<Motorista, Long> {
    List<Motorista> findAll();

    Optional<Motorista> findByIdMotorista(Long idMotorista);

    List<Motorista> findByStatus(StatusMotorista status);
}

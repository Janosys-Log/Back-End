package com.fleetops.motorista.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;

public interface MotoristaRepository extends JpaRepository<Motorista, Long> {
    List<Motorista> findAll();

    Optional<Motorista> findByIdMotorista(Long idMotorista);

    List<Motorista> findByStatus(StatusMotorista status);

    Motorista findBycpfMotorista(String cpf);

    boolean existsByCpfMotorista(String cpf);
}

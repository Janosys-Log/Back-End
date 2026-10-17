package com.fleetops.motorista.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;

public interface MotoristaRepository extends JpaRepository<Motorista, Long> {
    List<Motorista> findAll();

    Optional<Motorista> findByIdMotorista(Long idMotorista);

    List<Motorista> findByStatus(StatusMotorista status);

    Motorista findBycpfMotorista(String cpf);

    boolean existsByCpfMotorista(String cpf);
    List<Motorista> findByStatusAndVeiculoIdVeiculoIn(StatusMotorista status, List<Long> ids);

    List<Motorista> findByVeiculoIdVeiculoIn(List<Long> ids);

    @Query("SELECT m FROM Motorista m WHERE m.status = :status AND m.statusUpdatedAt <= :limite")
    List<Motorista> findIndisponiveisAntigos(
            @Param("status") StatusMotorista status, @Param("limite") LocalDateTime limite);
}

package com.fleetops.motorista.repository;

import com.fleetops.motorista.domain.Manifesto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManifestoRepositorio extends JpaRepository<Manifesto, Long> {
    Optional<Manifesto> findFirstByOrderByDataInsertDesc();

    @Query(
            """
    from Manifesto m
    where m.motorista.id = :motoristaId
      and month(m.dataManifesto) = :mes
      and year(m.dataManifesto) = :ano
    """)
    List<Manifesto> buscarPorMotoristaEMes(Long motoristaId, Long mes, Long ano);
}

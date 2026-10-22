package com.fleetops.avaliacao.repository;

import com.fleetops.avaliacao.domain.AvaliacaoFrete;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório de avaliações de frete.
 * Isolado no pacote avaliacao para futura extração em microsserviço.
 */
@Repository
public interface AvaliacaoFreteRepository extends JpaRepository<AvaliacaoFrete, Long> {
    Optional<AvaliacaoFrete> findById(Long id);

    List<AvaliacaoFrete> findByMotoristaIdOrderByDataRegistroDesc(Long motoristaId);
}

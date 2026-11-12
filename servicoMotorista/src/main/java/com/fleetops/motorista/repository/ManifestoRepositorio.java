package com.fleetops.motorista.repository;

import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.domain.RendimentoMensal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(
            """
      select new com.fleetops.motorista.domain.RendimentoMensal(
      year(m.dataManifesto),
      month(m.dataManifesto),
      sum(m.valorRecebido)/count(m),
      count(m),
      sum(m.valorRecebido)
      )
       from Manifesto m
      where m.dataManifesto between :inicio and :fim
      group by year(m.dataManifesto),month(m.dataManifesto)
      order by year(m.dataManifesto),month(m.dataManifesto)
      """)
    List<RendimentoMensal> RendimentoMedioPorMeses(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}

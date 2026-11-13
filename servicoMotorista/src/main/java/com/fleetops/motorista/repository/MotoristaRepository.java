package com.fleetops.motorista.repository;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.MotoristaRendimento;
import com.fleetops.motorista.domain.StatusMotorista;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MotoristaRepository extends JpaRepository<Motorista, Long> {
    List<Motorista> findAll();

    Optional<Motorista> findByIdMotorista(Long idMotorista);

    List<Motorista> findByStatus(StatusMotorista status);

    Motorista findBycpfMotorista(String cpf);

    boolean existsByCpfMotorista(String cpf);

    boolean existsByManifestoAtualAndIdMotoristaNot(Long manifestoAtual, Long idMotorista);

    List<Motorista> findByStatusAndVeiculoIdVeiculoIn(StatusMotorista status, List<Long> ids);

    List<Motorista> findByVeiculoIdVeiculoIn(List<Long> ids);

    @Query("SELECT m FROM Motorista m WHERE m.status = :status AND m.statusUpdatedAt <= :limite")
    List<Motorista> findIndisponiveisAntigos(
            @Param("status") StatusMotorista status, @Param("limite") LocalDateTime limite);

    @Query(
            """
    select new com.fleetops.motorista.domain.MotoristaRendimento(
        mo,
        year(mani.dataManifesto), month(mani.dataManifesto),
        avg(mani.valorRecebido), count(mani), sum(mani.valorRecebido))
    from Motorista mo
    join Manifesto mani on mani.motorista = mo
    where mani.dataManifesto between :inicio and :fim
    group by mo, year(mani.dataManifesto), month(mani.dataManifesto)
    order by avg(mani.valorRecebido) desc
    """)
    List<MotoristaRendimento> rankingMotoristas(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query(
            """
    select new com.fleetops.motorista.domain.MotoristaRendimento(
        mo,
        year(mani.dataManifesto), month(mani.dataManifesto),
        avg(mani.valorRecebido), count(mani), sum(mani.valorRecebido))
    from Motorista mo
    join Manifesto mani on mani.motorista = mo
    where mani.dataManifesto between :inicio and :fim
      and mani.destino in ('Sao Paulo', 'Diadema', 'Guarulhos', 'Sao Caetano', 'Santo Andre', 'Sao Bernardo do Campo')
    group by mo, year(mani.dataManifesto), month(mani.dataManifesto)
    order by avg(mani.valorRecebido) desc
    """)
    List<MotoristaRendimento> rankingMotoristasGrandeSp(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query(
            """
    SELECT (
        ((SELECT AVG(m.valorRecebido) FROM Manifesto m
          WHERE m.dataManifesto >= :mesAtual AND m.dataManifesto < :limiteMesAtual
            AND m.motorista.id = :id)
         -
         (SELECT AVG(m2.valorRecebido) FROM Manifesto m2
          WHERE m2.dataManifesto >= :mesAnterior AND m2.dataManifesto < :mesAtual
            AND m2.motorista.id = :id))
        * 100.0
        /
        (SELECT AVG(m3.valorRecebido) FROM Manifesto m3
         WHERE m3.dataManifesto >= :mesAnterior AND m3.dataManifesto < :mesAtual
           AND m3.motorista.id = :id)
    )
    FROM Motorista mot
    WHERE mot.id = :id
""")
    BigDecimal crescimentoRendaMedia(
            @Param("id") Long id,
            @Param("mesAnterior") LocalDate mesAnterior,
            @Param("mesAtual") LocalDate mesAtual,
            @Param("limiteMesAtual") LocalDate limiteMesAtual);

    @Query(
            """
SELECT (
    ((SELECT AVG(m.valorRecebido) FROM Manifesto m
      WHERE m.dataManifesto >= :mesAtual AND m.dataManifesto < :limiteMesAtual
        AND m.motorista.idMotorista = :id
        AND m.destino IN ('Sao Paulo', 'Diadema', 'Guarulhos', 'Sao Caetano', 'Santo Andre', 'Sao Bernardo do Campo'))
     -
     (SELECT AVG(m2.valorRecebido) FROM Manifesto m2
      WHERE m2.dataManifesto >= :mesAnterior AND m2.dataManifesto < :mesAtual
        AND m2.motorista.idMotorista = :id
        AND m2.destino IN ('Sao Paulo', 'Diadema', 'Guarulhos', 'Sao Caetano', 'Santo Andre', 'Sao Bernardo do Campo')))
    * 100.0
    /
    NULLIF((SELECT AVG(m3.valorRecebido) FROM Manifesto m3
     WHERE m3.dataManifesto >= :mesAnterior AND m3.dataManifesto < :mesAtual
       AND m3.motorista.idMotorista = :id
       AND m3.destino IN ('Sao Paulo', 'Diadema', 'Guarulhos', 'Sao Caetano', 'Santo Andre', 'Sao Bernardo do Campo')), 0)
)
FROM Motorista mot
WHERE mot.idMotorista = :id
""")
    BigDecimal crescimentoRendaMediaGrandeSP(
            @Param("id") Long id,
            @Param("mesAnterior") LocalDate mesAnterior,
            @Param("mesAtual") LocalDate mesAtual,
            @Param("limiteMesAtual") LocalDate limiteMesAtual);
}

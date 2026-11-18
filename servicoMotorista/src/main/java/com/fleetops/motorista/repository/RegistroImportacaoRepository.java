package com.fleetops.motorista.repository;

import com.fleetops.motorista.domain.RegistroImportacao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroImportacaoRepository extends JpaRepository<RegistroImportacao, Long> {
    Optional<RegistroImportacao> findFirstByOrderByDataHoraDesc();

    Optional<RegistroImportacao> findFirstByTipoArquivoOrderByDataHoraDesc(String tipoArquivo);
}

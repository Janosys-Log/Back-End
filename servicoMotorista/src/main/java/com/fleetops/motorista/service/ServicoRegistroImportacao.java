package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.RegistroImportacao;
import com.fleetops.motorista.dto.ResponseDate;
import com.fleetops.motorista.repository.RegistroImportacaoRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoRegistroImportacao {
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");

    @Autowired
    private RegistroImportacaoRepository repository;

    public RegistroImportacao registrarImportacao(
            String tipoArquivo, String nomeArquivo, String dataHoraStr, int qtdRegistros) {
        LocalDateTime dataHora = LocalDateTime.now(FUSO_BRASILIA);
        if (dataHoraStr != null && !dataHoraStr.isBlank()) {
            try {
                dataHora = LocalDateTime.parse(dataHoraStr);
            } catch (Exception ignored) {
            }
        }
        String nome = (nomeArquivo != null && !nomeArquivo.isBlank()) ? nomeArquivo : "arquivo.csv";

        RegistroImportacao registro = RegistroImportacao.builder()
                .tipoArquivo(tipoArquivo)
                .nomeArquivo(nome)
                .dataHora(dataHora)
                .qtdRegistros(qtdRegistros)
                .build();

        return repository.save(registro);
    }

    public Optional<ResponseDate> buscarUltimaImportacao(String tipoArquivo) {
        Optional<RegistroImportacao> registro;
        if (tipoArquivo != null && !tipoArquivo.isBlank() && !tipoArquivo.equalsIgnoreCase("TODOS")) {
            registro = repository.findFirstByTipoArquivoOrderByDataHoraDesc(tipoArquivo.toUpperCase());
        } else {
            registro = repository.findFirstByOrderByDataHoraDesc();
        }

        return registro.map(r -> new ResponseDate(r.getDataHora(), r.getNomeArquivo(), r.getTipoArquivo()));
    }
}

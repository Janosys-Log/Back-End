package com.fleetops.avaliacao.service;

import com.fleetops.avaliacao.domain.AvaliacaoFrete;
import com.fleetops.avaliacao.domain.EscalaNota;
import com.fleetops.avaliacao.dto.AvaliacaoRequestDTO;
import com.fleetops.avaliacao.dto.AvaliacaoResponseDTO;
import com.fleetops.avaliacao.repository.AvaliacaoFreteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Serviço de avaliações de frete.
 * Contém a lógica de negócio e permanece isolado no pacote avaliacao
 * para facilitar futura migração para microsserviço.
 */
@Service
@RequiredArgsConstructor
public class AvaliacaoFreteService {

    private final AvaliacaoFreteRepository repository;

    @Transactional
    public AvaliacaoResponseDTO registrar(AvaliacaoRequestDTO request) {
        // Validação adicional da escala (já coberta pelo @Min/@Max, mas reforça regra
        // de negócio)
        EscalaNota escala = EscalaNota.fromValor(request.getNota());

        AvaliacaoFrete avaliacao = AvaliacaoFrete.builder()
                .nota(request.getNota())
                .numeroManifesto(request.getNumeroManifesto())
                .feedback(request.getFeedback().trim())
                .motoristaId(request.getMotoristaId())
                .build();

        AvaliacaoFrete salva = repository.save(avaliacao);

        return AvaliacaoResponseDTO.builder()
                .mensagem("Avaliação registrada com sucesso")
                .id(salva.getId())
                .motoristaId(salva.getMotoristaId())
                .nota(salva.getNota())
                .numeroManifesto(salva.getNumeroManifesto())
                .descricaoNota(escala.getDescricao())
                .feedback(salva.getFeedback())
                .dataRegistro(salva.getDataRegistro())
                .build();
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponseDTO buscarPorId(Long id) {
        AvaliacaoFrete a = repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada: " + id));

        return AvaliacaoResponseDTO.builder()
                .id(a.getId())
                .motoristaId(a.getMotoristaId())
                .nota(a.getNota())
                .numeroManifesto(a.getNumeroManifesto())
                .descricaoNota(EscalaNota.fromValor(a.getNota()).getDescricao())
                .feedback(a.getFeedback())
                .dataRegistro(a.getDataRegistro())
                .build();
    }
}

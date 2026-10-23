package com.fleetops.avaliacao.service;

import com.fleetops.avaliacao.domain.AvaliacaoFrete;
import com.fleetops.avaliacao.domain.EscalaNota;
import com.fleetops.avaliacao.dto.AvaliacaoRequestDTO;
import com.fleetops.avaliacao.dto.AvaliacaoResponseDTO;
import com.fleetops.avaliacao.dto.AvaliacaoResponseDTOMapper;
import com.fleetops.avaliacao.exception.AvaliacaoNaoEncontrada;
import com.fleetops.avaliacao.repository.AvaliacaoFreteRepository;
import java.util.List;
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

    public List<AvaliacaoResponseDTO> listarAvaliacoes() {
        List<AvaliacaoFrete> avaliacoes = this.repository.findAll();
        return avaliacoes.stream()
                .map(a -> AvaliacaoResponseDTOMapper.toDTO(a, "", ""))
                .toList();
    }

    public AvaliacaoResponseDTO buscarAvaliacao(Long id) {
        AvaliacaoFrete avaliacao = this.repository
                .findById(id)
                .orElseThrow(() -> new AvaliacaoNaoEncontrada(id, "Não foi possível retornar avaliação"));

        return AvaliacaoResponseDTOMapper.toDTO(avaliacao, "", "");
    }

    @Transactional
    public AvaliacaoResponseDTO registrar(AvaliacaoRequestDTO request) {
        // Validação adicional da escala (já coberta pelo @Min/@Max, mas reforça regra de negócio)
        EscalaNota.fromValor(request.getNota());

        AvaliacaoFrete avaliacao = AvaliacaoFrete.builder()
                .nota(request.getNota())
                .numeroManifesto(request.getNumeroManifesto())
                .feedback(request.getFeedback().trim())
                .motoristaId(request.getMotoristaId())
                .build();

        AvaliacaoFrete salva = repository.save(avaliacao);

        AvaliacaoResponseDTO response = toResponse(salva);
        response.setMensagem("Avaliação registrada com sucesso");
        return response;
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponseDTO buscarPorId(Long id) {
        return repository
                .findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Avaliação não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> listarPorMotorista(Long motoristaId) {
        return repository.findByMotoristaIdOrderByDataRegistroDesc(motoristaId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AvaliacaoResponseDTO toResponse(AvaliacaoFrete a) {
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
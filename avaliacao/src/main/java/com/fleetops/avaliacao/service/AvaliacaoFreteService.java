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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<AvaliacaoResponseDTO> listarPorMotorista(Long motoristaId) {
        return this.repository.findByMotoristaIdOrderByDataRegistroDesc(motoristaId).stream()
                .map(a -> AvaliacaoResponseDTOMapper.toDTO(a, "", ""))
                .toList();
    }

    public AvaliacaoResponseDTO buscarAvaliacao(Long id) {
        AvaliacaoFrete avaliacao = this.repository
                .findById(id)
                .orElseThrow(() -> new AvaliacaoNaoEncontrada(id, "Não foi possível retornar avaliação"));

        return AvaliacaoResponseDTOMapper.toDTO(avaliacao, "", "");
    }

    /**
     * Monta a entidade a partir do request.
     * Não valida existência do manifesto na base importada:
     * o número digitado no modal de "colocar em rota" é gravado em num_modal.
     */
    public AvaliacaoFrete verificacaAvaliacao(AvaliacaoRequestDTO request) {
        return AvaliacaoFrete.builder()
                .nota(request.getNota())
                .numeroManifesto(request.getNumeroManifesto())
                .feedback(request.getFeedback().trim())
                .motoristaId(request.getMotoristaId())
                .build();
    }

    @Transactional
    public AvaliacaoResponseDTO registrar(AvaliacaoFrete avaliacao) {
        EscalaNota escala = EscalaNota.fromValor(avaliacao.getNota());
        AvaliacaoFrete salva = repository.save(avaliacao);

        return AvaliacaoResponseDTOMapper.toDTO(salva, "Avaliação registrada com sucesso", escala.getDescricao());
    }
}

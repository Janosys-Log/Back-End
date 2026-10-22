package com.fleetops.avaliacao.dto;

import com.fleetops.avaliacao.domain.AvaliacaoFrete;

public class AvaliacaoResponseDTOMapper {
    public static AvaliacaoResponseDTO toDTO(AvaliacaoFrete avaliacao, String mensagem, String descricaoNota) {
        return AvaliacaoResponseDTO.builder()
                .mensagem(mensagem)
                .id(avaliacao.getId())
                .motoristaId(avaliacao.getMotoristaId())
                .nota(avaliacao.getNota())
                .numeroManifesto(avaliacao.getNumeroManifesto())
                .descricaoNota(descricaoNota)
                .feedback(avaliacao.getFeedback())
                .dataRegistro(avaliacao.getDataRegistro())
                .build();
    }
}

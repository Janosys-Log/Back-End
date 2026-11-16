package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;
import java.util.List;

public record DetalheMotoristaManifestosResponseDTO(
        Long motoristaId,
        String nomeMotorista,
        String subtipoLabel,
        String mesReferencia,
        VisaoRanking visao,
        int posicao,
        String posicaoFormatada,
        BigDecimal rentabilidadeMedia,
        BigDecimal rentabilidadeTotal,
        int totalManifestos,
        int manifestosNegativos,
        String fracaoManifestosNegativos,
        List<ManifestoItemDetalheDTO> manifestos) {}

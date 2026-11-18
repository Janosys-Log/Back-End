package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;

public record MotoristaNegativoItemDTO(
        int posicao,
        String posicaoFormatada,
        Long motoristaId,
        String nomeMotorista,
        String cpfMotorista,
        String tipoVeiculo,
        String subtipoVeiculo,
        String subtipoLabel,
        BigDecimal rentabilidadeMedia,
        BigDecimal variacaoMesAnterior,
        String statusVariacao,
        int numeroManifestos,
        int manifestosNegativos,
        String fracaoManifestosNegativos,
        BigDecimal rentabilidadeTotal) {}

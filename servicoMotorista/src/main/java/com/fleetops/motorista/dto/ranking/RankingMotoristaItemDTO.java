package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;

public record RankingMotoristaItemDTO(
        int posicao,
        Long motoristaId,
        String nomeMotorista,
        String cpfMotorista,
        String tipoVeiculo,
        String subtipoVeiculo,
        String subtipoLabel,
        int numeroManifestos,
        BigDecimal rentabilidadeMedia,
        BigDecimal variacaoMesAnterior,
        String statusVariacao,
        BigDecimal margemPercentual,
        BigDecimal rentabilidadeTotal,
        boolean top3,
        boolean ultimos3,
        boolean abaixoMediaFrota,
        boolean rentabilidadeNegativa,
        String corDestaque) {}

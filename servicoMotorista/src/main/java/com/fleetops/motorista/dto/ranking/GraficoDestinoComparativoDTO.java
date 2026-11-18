package com.fleetops.motorista.dto.ranking;

import java.math.BigDecimal;

public record GraficoDestinoComparativoDTO(
        BigDecimal mediaFrotaGeralMes,
        BigDecimal rentabilidadeMediaGrandeSp,
        int manifestosGrandeSp,
        String corGrandeSp,
        BigDecimal rentabilidadeMediaDemaisDestinos,
        int manifestosDemaisDestinos,
        String corDemaisDestinos) {}

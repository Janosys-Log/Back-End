package com.fleetops.motorista.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record MotoristaRendimento(Motorista motorista, RendimentoMensal rendimento) {

    public MotoristaRendimento(
            Motorista motorista, Integer ano, Integer mes, Double media, Long numeroManifestos, BigDecimal total) {
        this(
                motorista,
                new RendimentoMensal(
                        ano,
                        mes,
                        BigDecimal.valueOf(media).setScale(2, RoundingMode.HALF_UP),
                        numeroManifestos,
                        total));
    }
}

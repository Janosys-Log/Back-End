package com.fleetops.CsvParaJson.converters;

import com.fleetops.CsvParaJson.enums.TipoVeiculo;
import com.univocity.parsers.conversions.Conversion;

public class TipoVeiculoConversion implements Conversion<String, TipoVeiculo> {

    @Override
    public TipoVeiculo execute(String input) {
        return TipoVeiculo.fromString(input);
    }

    @Override
    public String revert(TipoVeiculo input) {
        return input != null ? input.getDescricao() : null;
    }
}

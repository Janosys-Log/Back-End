package com.fleetops.CsvParaJson.converters;

import com.fleetops.CsvParaJson.enums.SubtipoVeiculo;
import com.univocity.parsers.conversions.Conversion;

public class SubtipoVeiculoConversion implements Conversion<String, String> {

    @Override
    public String execute(String input) {
        SubtipoVeiculo subtipo = SubtipoVeiculo.fromString(input);
        return subtipo != null ? subtipo.name() : null;
    }

    @Override
    public String revert(String input) {
        return input;
    }
}

package com.fleetops.CsvParaJson.converters;

import com.univocity.parsers.conversions.Conversion;

public class MotoristaCleanerConversion implements Conversion<String, String> {

    @Override
    public String execute(String input) {
        if (input == null || input.isBlank()) {
            return "NÃO INFORMADO";
        }
        return input.trim();
    }

    @Override
    public String revert(String input) {
        return input;
    }
}

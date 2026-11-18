package com.fleetops.CsvParaJson.converters;

import com.univocity.parsers.conversions.Conversion;

public class PlacaCleanerConversion implements Conversion<String, String> {

    @Override
    public String execute(String input) {
        if (input == null) {
            return null;
        }
        String placa = input.replaceAll("[^A-Za-z0-9]", "").toUpperCase().trim();
        return placa.isBlank() ? null : placa;
    }

    @Override
    public String revert(String input) {
        return input;
    }
}

package com.fleetops.CsvParaJson.converters;

import com.univocity.parsers.conversions.Conversion;

public class DigitsOnlyConversion implements Conversion<String, String> {

    @Override
    public String execute(String input) {
        if (input == null) {
            return null;
        }
        String digits = input.replaceAll("[^0-9]", "");
        return digits.isBlank() ? null : digits;
    }

    @Override
    public String revert(String input) {
        return input;
    }
}

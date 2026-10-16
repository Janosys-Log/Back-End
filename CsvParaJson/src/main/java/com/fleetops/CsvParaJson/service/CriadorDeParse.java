package com.fleetops.CsvParaJson.service;

import com.univocity.parsers.common.DataProcessingException;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.RetryableErrorHandler;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.stereotype.Component;

@Component
public class CriadorDeParse<T> {

    public CsvParser getParser(BeanListProcessor<T> processor) {
        CsvParserSettings csvSettings = new CsvParserSettings();
        csvSettings.setHeaderExtractionEnabled(true);
        csvSettings.detectFormatAutomatically();
        csvSettings.setIgnoreLeadingWhitespaces(true);
        csvSettings.setIgnoreTrailingWhitespaces(true);
        csvSettings.setNullValue("");
        csvSettings.setProcessor(processor);
        csvSettings.setProcessorErrorHandler(new RetryableErrorHandler<ParsingContext>() {
    public void handleError(DataProcessingException ex, Object[] row, ParsingContext ctx) { setDefaultValue(null); }});
        return new CsvParser(csvSettings);
    }
}

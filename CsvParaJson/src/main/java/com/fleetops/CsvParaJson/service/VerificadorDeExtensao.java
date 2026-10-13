package com.fleetops.CsvParaJson.service;

import org.springframework.stereotype.Component;

@Component
public class VerificadorDeExtensao {
    public Boolean ECsv(String nomeDoArquivo) {
        String[] splitDoNome = nomeDoArquivo.split("\\.");
        String extensao = splitDoNome[splitDoNome.length - 1];
        return extensao.equals("csv");
    }
}

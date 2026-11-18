package com.fleetops.CsvParaJson.controller;

import com.fleetops.CsvParaJson.config.RabbitConfig;
import com.fleetops.CsvParaJson.dto.response.ManifestoDTO;
import com.fleetops.CsvParaJson.dto.response.ResultadoCsv;
import com.fleetops.CsvParaJson.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.CsvParaJson.dto.response.VeiculoDTO;
import com.fleetops.CsvParaJson.exceptions.tipos.ArquivoInexistente;
import com.fleetops.CsvParaJson.service.CsvParaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CsvVeiculosController {
    @Autowired
    private CsvParaJson<VeiculoDTO> csvParaVeiculo;

    @Autowired
    private CsvParaJson<ManifestoDTO> csvParaManifesto;

    @PostMapping("/csv")
    public ResultadoCsv<VeiculoDTO, ManifestoDTO> csvParaJsonVeiculo(
            @RequestParam(value = "csvVeiculo", required = false) MultipartFile csvVeiculo,
            @RequestParam(value = "csvManifesto", required = false) MultipartFile csvManifesto) {

        boolean temVeiculo = csvVeiculo != null && !csvVeiculo.isEmpty();
        boolean temManifesto = csvManifesto != null && !csvManifesto.isEmpty();

        if (!temVeiculo && !temManifesto) {
            throw new ArquivoInexistente(
                    "Nenhum arquivo enviado", "Envie pelo menos o arquivo CSV de veículos ou o de manifestos.");
        }

        ResultadoLinhasDoCsv<VeiculoDTO> veiculos = null;
        if (temVeiculo) {
            veiculos = csvParaVeiculo.RetornarJson(csvVeiculo, VeiculoDTO.class, RabbitConfig.routing_key_veiculosJson);
        }

        ResultadoLinhasDoCsv<ManifestoDTO> manifestos = null;
        if (temManifesto) {
            manifestos = csvParaManifesto.RetornarJson(
                    csvManifesto, ManifestoDTO.class, RabbitConfig.routing_key_ManifestoJson);
        }

        return new ResultadoCsv<>(veiculos, manifestos);
    }
}

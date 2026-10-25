package com.fleetops.CsvParaJson.controller;

import com.fleetops.CsvParaJson.config.RabbitConfig;
import com.fleetops.CsvParaJson.dto.response.ManifestoDTO;
import com.fleetops.CsvParaJson.dto.response.ResultadoCsv;
import com.fleetops.CsvParaJson.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.CsvParaJson.dto.response.VeiculoDTO;
import com.fleetops.CsvParaJson.service.CsvParaJson;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CsvVeiculosController {
    @Autowired
    CsvParaJson<VeiculoDTO> csvParaVeiculo;

    @Autowired
    CsvParaJson<ManifestoDTO> csvParaManifesto;

    @PostMapping("/csv")
    public ResultadoCsv<VeiculoDTO, ManifestoDTO> csvParaJsonVeiculo(
            @PathParam("csvVeiculo") MultipartFile csvVeiculo, @PathParam("csvManifesto") MultipartFile csvManifesto) {
        ResultadoLinhasDoCsv<VeiculoDTO> veiculos =
                csvParaVeiculo.RetornarJson(csvVeiculo, VeiculoDTO.class, RabbitConfig.routing_key_veiculosJson);
        ResultadoLinhasDoCsv<ManifestoDTO> manifestos =
                csvParaManifesto.RetornarJson(csvManifesto, ManifestoDTO.class, RabbitConfig.routing_key_ManifestoJson);
        return new ResultadoCsv<VeiculoDTO, ManifestoDTO>(veiculos, manifestos);
    }
}

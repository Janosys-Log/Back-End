package com.fleetops.CsvParaJson.controller;

import com.fleetops.CsvParaJson.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.CsvParaJson.dto.response.VeiculoDTO;
import com.fleetops.CsvParaJson.service.CsvParaJson;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VeiculosController {
    @Autowired
    CsvParaJson<VeiculoDTO> csv;

    @PostMapping("/arquivo")
    public ResultadoLinhasDoCsv<VeiculoDTO> csvParaJson(@PathParam("arquivo") MultipartFile arquivo) {
        return csv.RetornarJson(arquivo, VeiculoDTO.class);
    }
}

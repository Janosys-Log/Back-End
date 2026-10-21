package com.fleetops.motorista.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fleetops.motorista.domain.SubtipoVeiculo;
import com.fleetops.motorista.domain.TipoVeiculo;
import com.fleetops.motorista.dto.VeiculoDTO;
import com.fleetops.motorista.service.ServicoVeiculo;

@RestController
@RequestMapping("/api/veiculos")
@CrossOrigin(origins = "*")
public class VeiculoController {
    @Autowired 
    private ServicoVeiculo servico;

    @GetMapping
    public List<VeiculoDTO> listar(
            @RequestParam(required = false) TipoVeiculo tipo,
            @RequestParam(required = false) SubtipoVeiculo subtipo) {
        return servico.listar(tipo, subtipo).stream()
                .map(VeiculoDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoDTO> buscarPorId(@PathVariable Long id) {
        return servico.buscarPorId(id)
                .map(VeiculoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    public List<VeiculoDTO> listarPorTipo(@PathVariable TipoVeiculo tipo) {
        return servico.listarPorTipo(tipo).stream()
                .map(VeiculoDTO::fromEntity)
                .toList();
    }
}
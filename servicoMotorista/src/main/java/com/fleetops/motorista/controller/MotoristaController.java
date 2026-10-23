package com.fleetops.motorista.controller;

import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.dto.MotoristaDTO;
import com.fleetops.motorista.service.ServicoMotorista;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/motoristas")
@CrossOrigin(origins = "*")
public class MotoristaController {
    @Autowired
    private ServicoMotorista servico;

    @GetMapping
    public ResponseEntity<List<MotoristaDTO>> listar(
            @RequestParam(required = false) StatusMotorista status,
            @RequestParam(required = false) List<Long> veiculoIds) {
        List<MotoristaDTO> resultado = servico.listar(status, veiculoIds).stream()
                .map(MotoristaDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/disponiveis")
    public List<MotoristaDTO> listarDisponiveis() {
        return servico.listarDisponiveis().stream()
                .map(MotoristaDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoristaDTO> buscarPorId(@PathVariable Long id) {
        return servico.buscarPorId(id)
                .map(MotoristaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

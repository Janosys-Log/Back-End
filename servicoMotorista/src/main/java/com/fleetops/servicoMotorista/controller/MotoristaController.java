package com.fleetops.servicoMotorista.controller;

import com.fleetops.servicoMotorista.domain.Motorista;
import com.fleetops.servicoMotorista.domain.StatusMotorista;
import com.fleetops.servicoMotorista.repository.MotoristaRepository;
import java.util.List;
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

    private final MotoristaRepository motoristaRepository;

    public MotoristaController(MotoristaRepository motoristaRepository) {
        this.motoristaRepository = motoristaRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) StatusMotorista status,
            @RequestParam(required = false) List<Long> veiculoIds) {
        List<Motorista> resultado;
        if (status != null && veiculoIds != null && !veiculoIds.isEmpty()) {
            resultado = motoristaRepository.findByStatusAndVeiculoIdVeiculoIn(status, veiculoIds);
        } else if (status != null) {
            resultado = motoristaRepository.findByStatus(status);
        } else if (veiculoIds != null && !veiculoIds.isEmpty()) {
            resultado = motoristaRepository.findByVeiculoIdVeiculoIn(veiculoIds);
        } else {
            resultado = motoristaRepository.findAll();
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/disponiveis")
    public List<Motorista> listarDisponiveis() {
        return motoristaRepository.findByStatus(StatusMotorista.Disponivel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Motorista> buscarPorId(@PathVariable Long id) {
        return motoristaRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

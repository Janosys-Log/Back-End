package com.fleetops.motorista.controller;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.repository.MotoristaRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<Motorista> listarTodos() {
        return motoristaRepository.findAll();
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

    @PostMapping
    public ResponseEntity<Motorista> criar(@RequestBody Motorista novoMotorista) {
        Motorista salvo = motoristaRepository.save(novoMotorista);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // @PatchMapping("/{id}/status")
    // public ResponseEntity<Motorista> atualizarStatus(@PathVariable Long id,
    // @RequestBody Map<String, String> payload)
    // {
    // return motoristaRepository
    // .findById(id)
    // .map(motorista -> {
    // if (payload.containsKey("status")) {
    // // Converte o texto recebido no JSON para o Enum correspondente:
    // StatusMotorista novoStatus =
    // StatusMotorista.valueOf(payload.get("status").toUpperCase());
    // motorista.setStatus(novoStatus);
    // }
    // Motorista atualizado = motoristaRepository.save(motorista);
    // return ResponseEntity.ok(atualizado);
    // })
    // .orElse(ResponseEntity.notFound().build());
    // }
}

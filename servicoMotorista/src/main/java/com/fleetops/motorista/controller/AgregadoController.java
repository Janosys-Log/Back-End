package com.fleetops.motorista.controller;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.repository.AgregadoRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agregados")
@CrossOrigin(origins = "*")
public class AgregadoController {

    private final AgregadoRepository repositorio;

    public AgregadoController(AgregadoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public List<Agregado> listarTodos() {
        return repositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agregado> buscarPorId(@PathVariable Long id) {
        return repositorio
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

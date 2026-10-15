package com.fleetops.veiculo.controller;

import com.fleetops.veiculo.domain.SubtipoVeiculo;
import com.fleetops.veiculo.domain.TipoVeiculo;
import com.fleetops.veiculo.domain.Veiculo;
import com.fleetops.veiculo.repository.VeiculoRepository;
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
@RequestMapping("/api/veiculos")
@CrossOrigin(origins = "*")
public class VeiculoController {

    private final VeiculoRepository repositorio;

    public VeiculoController(VeiculoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public List<Veiculo> listarTodos() {
        return repositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscarPorId(@PathVariable Long id) {
        return repositorio
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Veiculo> criar(@jakarta.validation.Valid @RequestBody Veiculo novoVeiculo) {
        novoVeiculo.setIdVeiculo(null);
        Veiculo salvo = repositorio.save(novoVeiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/tipo/{tipo}")
    public List<Veiculo> listarPorTipo(@PathVariable("tipo") TipoVeiculo tipo) {
        return repositorio.findByTipoVeiculo(tipo);
    }

    @GetMapping("/subtipo/{subtipo}")
    public List<Veiculo> listarPorSubtipo(@PathVariable("subtipo") SubtipoVeiculo subtipo) {
        return repositorio.findBySubtipoVeiculo(subtipo);
    }

    @GetMapping("/subtipos-por-tipo/{tipo}")
    public List<SubtipoVeiculo> listarSubtiposPorTipo(@PathVariable("tipo") TipoVeiculo tipo) {
        return SubtipoVeiculo.listarPorTipo(tipo);
    }
}

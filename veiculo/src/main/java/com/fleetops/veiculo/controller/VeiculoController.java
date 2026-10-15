package com.fleetops.veiculo.controller;

import com.fleetops.veiculo.domain.CategoriaVeiculo;
import com.fleetops.veiculo.domain.TipoVeiculos;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<Veiculo> listar(
            @RequestParam(required = false) CategoriaVeiculo categoria,
            @RequestParam(required = false) TipoVeiculos tipo) {
        if (tipo != null) {
            return repositorio.findByTipoVeiculo(tipo);
        }
        if (categoria != null) {
            return repositorio.findByCategoria(categoria);
        }
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
    public ResponseEntity<Veiculo> criar(@RequestBody Veiculo novoVeiculo) {
        if (novoVeiculo.getTipoVeiculo() != null && novoVeiculo.getCategoria() == null) {
            novoVeiculo.setCategoria(novoVeiculo.getTipoVeiculo().getCategoria());
        }
        Veiculo salvo = repositorio.save(novoVeiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/tipo/{tipo}")
    public List<Veiculo> listarPorTipo(@PathVariable TipoVeiculos tipo) {
        return repositorio.findByTipoVeiculo(tipo);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Veiculo> listarPorCategoria(@PathVariable CategoriaVeiculo categoria) {
        return repositorio.findByCategoria(categoria);
    }
}

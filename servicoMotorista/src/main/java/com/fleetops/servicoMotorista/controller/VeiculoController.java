package com.fleetops.servicoMotorista.controller;

import com.fleetops.servicoMotorista.domain.SubtipoVeiculo;
import com.fleetops.servicoMotorista.domain.TipoVeiculo;
import com.fleetops.servicoMotorista.domain.Veiculo;
import com.fleetops.servicoMotorista.repository.VeiculoRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestParam(required = false) TipoVeiculo tipo, @RequestParam(required = false) SubtipoVeiculo subtipo) {
        if (tipo != null) {
            return repositorio.findByTipoVeiculo(tipo);
        }
        if (subtipo != null) {
            return repositorio.findBySubtipoVeiculo(subtipo);
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

    @GetMapping("/tipo/{tipo}")
    public List<Veiculo> listarPorTipo(@PathVariable TipoVeiculo tipo) {
        return repositorio.findByTipoVeiculo(tipo);
    }
}

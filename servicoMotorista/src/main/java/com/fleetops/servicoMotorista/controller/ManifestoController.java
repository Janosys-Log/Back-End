package com.fleetops.servicoMotorista.controller;

import com.fleetops.servicoMotorista.domain.Manifesto;
import com.fleetops.servicoMotorista.dto.ResponseDate;
import com.fleetops.servicoMotorista.repository.ManifestoRepositorio;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manifestos")
@CrossOrigin(origins = "*")
public class ManifestoController {

    private final ManifestoRepositorio repositorio;

    public ManifestoController(ManifestoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public List<Manifesto> listarTodos() {
        return repositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Manifesto> buscarPorId(@PathVariable Long id) {
        return repositorio
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/dataDoUltimoManifesto")
    public ResponseEntity<ResponseDate> buscardataUltimoManifesto() {
        LocalDate data = repositorio.findFirstByOrderByDataInsert().get().getDataInsert();
        ResponseDate resposta = new ResponseDate(data);
        return ResponseEntity.ok(resposta);
    }
}

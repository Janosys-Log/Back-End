package com.fleetops.motorista.controller;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.dto.AgregadoOperacionalDTO;
import com.fleetops.motorista.repository.AgregadoRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public List<?> listarTodos(Authentication authentication) {
        boolean gerencia = ehGerencia(authentication);
        return repositorio.findAll().stream().map(agregado -> paraPerfil(agregado, gerencia)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        boolean gerencia = ehGerencia(authentication);
        return repositorio
                .findById(id)
                .map(agregado -> paraPerfil(agregado, gerencia))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private static Object paraPerfil(Agregado agregado, boolean gerencia) {
        return gerencia ? agregado : AgregadoOperacionalDTO.fromEntity(agregado);
    }

    private static boolean ehGerencia(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_GERENCIA".equals(authority.getAuthority()));
    }
}

package com.fleetops.usuario.controller;

import com.fleetops.usuario.dto.UsuarioCreateRequest;
import com.fleetops.usuario.dto.UsuarioResponse;
import com.fleetops.usuario.dto.UsuarioUpdateRequest;
import com.fleetops.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gestão de usuários (exclusivo da Gerência)")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuários (Requer GERENCIA)")
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca usuário por ID (Requer GERENCIA)")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cria um novo usuário (Requer GERENCIA)")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita um usuário; a senha só muda se for enviada (Requer GERENCIA)")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativa um usuário, sem excluir o registro (Requer GERENCIA)")
    public UsuarioResponse desativar(@PathVariable Long id, Authentication authentication) {
        return service.desativar(id, authentication.getName());
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Reativa um usuário (Requer GERENCIA)")
    public UsuarioResponse ativar(@PathVariable Long id) {
        return service.ativar(id);
    }
}

package com.fleetops.usuario.controller;

import com.fleetops.usuario.domain.Usuario;
import com.fleetops.usuario.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuários", description = "Gestão de usuários do sistema (exclusivo para Gerência)")
public class UsuarioController {

    private final UsuarioRepository repositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository repositorio, PasswordEncoder passwordEncoder) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuários cadastrados (Requer GERENCIA)")
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = repositorio.findAll();
        usuarios.forEach(u -> u.setSenhaUsuario(null));
        return usuarios;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca usuário por ID (Requer GERENCIA)")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return repositorio
                .findById(id)
                .map(u -> {
                    u.setSenhaUsuario(null);
                    return ResponseEntity.ok(u);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo usuário com senha criptografada (Requer GERENCIA)")
    public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario novoUsuario) {
        novoUsuario.setIdUsuario(null);
        if (novoUsuario.getSenhaUsuario() != null) {
            novoUsuario.setSenhaUsuario(passwordEncoder.encode(novoUsuario.getSenhaUsuario()));
        }
        if (novoUsuario.getAtivo() == null) {
            novoUsuario.setAtivo(true);
        }
        Usuario salvo = repositorio.save(novoUsuario);
        salvo.setSenhaUsuario(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
}

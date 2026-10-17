package com.fleetops.usuario.controller;

import com.fleetops.usuario.domain.Usuario;
import com.fleetops.usuario.dto.ErrorResponseDTO;
import com.fleetops.usuario.dto.LoginRequestDTO;
import com.fleetops.usuario.dto.LoginResponseDTO;
import com.fleetops.usuario.repository.UsuarioRepository;
import com.fleetops.usuario.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "Endpoints para login e autenticação de usuários")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Realiza autenticação de usuário e gera token JWT",
            description = "Valida as credenciais (e-mail e senha) e retorna o token de acesso com validade de 8 horas.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
                @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou usuário inativo")
            })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailUsuario(request.email());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Usuário ou senha inválidos."));
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaUsuario())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Usuário ou senha inválidos."));
        }

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Usuário inativo. Contate o administrador."));
        }

        String token = jwtService.gerarToken(usuario);
        long expiraEm = System.currentTimeMillis() + jwtService.getTempoExpiracaoMs();

        LoginResponseDTO response = new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNomeUsuario(),
                usuario.getEmailUsuario(),
                usuario.getPerfilAcesso(),
                expiraEm);

        return ResponseEntity.ok(response);
    }
}

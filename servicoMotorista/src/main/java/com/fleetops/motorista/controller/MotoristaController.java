package com.fleetops.motorista.controller;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.dto.AtualizarContatoDTO;
import com.fleetops.motorista.dto.ContatoMotoristaDTO;
import com.fleetops.motorista.dto.MotoristaDTO;
import com.fleetops.motorista.dto.MotoristaOperacionalDTO;
import com.fleetops.motorista.dto.MotoristaRendimentoDTO;
import com.fleetops.motorista.service.ServicoContatoMotorista;
import com.fleetops.motorista.service.ServicoMotorista;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/motoristas")
@CrossOrigin(origins = "*")
public class MotoristaController {
    @Autowired
    private ServicoMotorista servico;

    @Autowired
    private ServicoContatoMotorista servicoContato;

    @GetMapping
    public ResponseEntity<List<?>> listar(
            @RequestParam(required = false) StatusMotorista status,
            @RequestParam(required = false) List<Long> veiculoIds,
            Authentication authentication) {
        boolean gerencia = ehGerencia(authentication);
        List<?> resultado = servico.listar(status, veiculoIds).stream()
                .map(motorista -> paraPerfil(motorista, gerencia))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/disponiveis")
    public List<?> listarDisponiveis(Authentication authentication) {
        boolean gerencia = ehGerencia(authentication);
        return servico.listarDisponiveis().stream()
                .map(motorista -> paraPerfil(motorista, gerencia))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        boolean gerencia = ehGerencia(authentication);
        return servico.buscarPorId(id)
                .map(motorista -> paraPerfil(motorista, gerencia))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private static Object paraPerfil(Motorista motorista, boolean gerencia) {
        return gerencia ? MotoristaDTO.fromEntity(motorista) : MotoristaOperacionalDTO.fromEntity(motorista);
    }

    private static boolean ehGerencia(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_GERENCIA".equals(authority.getAuthority()));
    }

    @GetMapping("/rankingPorData/{inicio}/{fim}")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<List<MotoristaRendimentoDTO>> rankingMotorista(
            @PathVariable LocalDate inicio, @PathVariable LocalDate fim) {

        return ResponseEntity.ok(servico.rankingMotorista(inicio, fim));
    }

    @GetMapping("/rankingPorDataGrandeSp/{inicio}/{fim}")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<List<MotoristaRendimentoDTO>> rankingMotoristaGrandeSP(
            @PathVariable LocalDate inicio, @PathVariable LocalDate fim) {

        return ResponseEntity.ok(servico.rankingMotoristaGrandeSp(inicio, fim));
    }

    @GetMapping("/contatos")
    public ResponseEntity<Map<Long, ContatoMotoristaDTO>> listarContatosAtivos() {
        return ResponseEntity.ok(servicoContato.listarContatosAtivos());
    }

    @PostMapping("/{id}/contato")
    public ResponseEntity<ContatoMotoristaDTO> adquirirContato(
            @PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        String email = authentication.getName();
        Object nomeAttr = request.getAttribute("usuarioNome");
        String nome = (nomeAttr instanceof String s && !s.isBlank()) ? s : email;

        ServicoContatoMotorista.ResultadoLock resultado = servicoContato.adquirirOuRenovar(id, email, nome);
        if (resultado.adquirido()) {
            return ResponseEntity.ok(resultado.contato());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resultado.contato());
    }

    @DeleteMapping("/{id}/contato")
    public ResponseEntity<Void> liberarContato(@PathVariable Long id, Authentication authentication) {
        servicoContato.liberar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/contatos/meus")
    public ResponseEntity<Void> liberarMeusContatos(Authentication authentication) {
        servicoContato.liberarTodosDoUsuario(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/telefone")
    public ResponseEntity<?> atualizarTelefone(
            @PathVariable Long id, @Valid @RequestBody AtualizarContatoDTO dto, Authentication authentication) {
        if (authentication != null) {
            Optional<ContatoMotoristaDTO> contato = servicoContato.obterContatoAtivo(id);
            if (contato.isPresent() && !contato.get().email().equalsIgnoreCase(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "erro",
                                "Motorista em contato com " + contato.get().nome() + " — aguarde a liberação."));
            }
        }
        boolean gerencia = ehGerencia(authentication);
        return servico.atualizarContato(id, dto)
                .<ResponseEntity<?>>map(motorista -> ResponseEntity.ok(paraPerfil(motorista, gerencia)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

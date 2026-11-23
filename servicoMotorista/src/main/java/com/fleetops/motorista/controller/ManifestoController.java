package com.fleetops.motorista.controller;

import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.dto.CardsCustoDTO;
import com.fleetops.motorista.dto.ManifestoDTO;
import com.fleetops.motorista.dto.ManifestoOperacionalDTO;
import com.fleetops.motorista.dto.RentabilidadeDTO;
import com.fleetops.motorista.dto.ResponseDate;
import com.fleetops.motorista.service.ServicoManifesto;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manifestos")
@CrossOrigin(origins = "*")
public class ManifestoController {
    @Autowired
    private ServicoManifesto servico;

    @Autowired
    private com.fleetops.motorista.service.ServicoRegistroImportacao servicoRegistroImportacao;

    @GetMapping
    public List<?> listarTodos(Authentication authentication) {
        boolean gerencia = ehGerencia(authentication);
        return servico.listarTodos().stream()
                .map(manifesto -> paraPerfil(manifesto, gerencia))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        boolean gerencia = ehGerencia(authentication);
        return servico.buscarPorId(id)
                .map(manifesto -> paraPerfil(manifesto, gerencia))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private static Object paraPerfil(Manifesto manifesto, boolean gerencia) {
        return gerencia ? ManifestoDTO.fromEntity(manifesto) : ManifestoOperacionalDTO.fromEntity(manifesto);
    }

    private static boolean ehGerencia(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_GERENCIA".equals(authority.getAuthority()));
    }

    @GetMapping("/dataDoUltimoManifesto")
    public ResponseEntity<ResponseDate> buscarDataUltimoManifesto() {
        return servicoRegistroImportacao
                .buscarUltimaImportacao(null)
                .or(() -> servico.buscarDataUltimoManifesto())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ultimaImportacao")
    public ResponseEntity<ResponseDate> buscarUltimaImportacao(
            @RequestParam(value = "tipo", required = false) String tipo) {
        return servicoRegistroImportacao
                .buscarUltimaImportacao(tipo)
                .or(() -> servico.buscarDataUltimoManifesto())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rentabilidade/{id}/{mes}/{ano}")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<RentabilidadeDTO> rentabilidadeMediaPorIdMotorista(
            @PathVariable Long id, @PathVariable Long mes, @PathVariable Long ano) {
        RentabilidadeDTO rentabilidade = servico.RentabilidademanifestosPorMotoristaEAnoEMes(id, mes, ano);

        return ResponseEntity.ok(rentabilidade);
    }

    @GetMapping("/rentabilidadeGeral/{inicio}/{fim}")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<HashMap<Integer, RentabilidadeDTO>> rentabilidadeGeralDaEmpresa(
            @PathVariable LocalDate inicio, @PathVariable LocalDate fim) {
        HashMap<Integer, RentabilidadeDTO> dadosDeAcordoComAsDatas = servico.RentabilidadeNoUltimosMeses(inicio, fim);
        return ResponseEntity.ok(dadosDeAcordoComAsDatas);
    }

    @GetMapping("/rentabilidadeGrandeSp/{id}/{mes}/{ano}")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<RentabilidadeDTO> rentabilidadeMediaPorIdMotoristaGrandeSP(
            @PathVariable Long id, @PathVariable Long mes, @PathVariable Long ano) {
        RentabilidadeDTO rentabilidade = servico.RentabilidademanifestosPorMotoristaEAnoEMesGrandeSP(id, mes, ano);

        return ResponseEntity.ok(rentabilidade);
    }

    @GetMapping("/rentabilidadeGeralGrandeSp/{inicio}/{fim}")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<HashMap<Integer, RentabilidadeDTO>> rentabilidadeGeralDaEmpresaGrandeSP(
            @PathVariable LocalDate inicio, @PathVariable LocalDate fim) {
        HashMap<Integer, RentabilidadeDTO> dadosDeAcordoComAsDatas =
                servico.RentabilidadeNoUltimosMesesGrandeSp(inicio, fim);
        return ResponseEntity.ok(dadosDeAcordoComAsDatas);
    }

    @GetMapping("/dashboard/cards-custo")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<CardsCustoDTO> buscarCardsCusto(@RequestParam(required = false) String mes) {
        return ResponseEntity.ok(servico.obterCardsCusto(mes));
    }
}

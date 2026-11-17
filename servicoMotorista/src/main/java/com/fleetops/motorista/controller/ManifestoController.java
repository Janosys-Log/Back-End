package com.fleetops.motorista.controller;

import com.fleetops.motorista.dto.CardsCustoDTO;
import com.fleetops.motorista.dto.ManifestoDTO;
import com.fleetops.motorista.dto.RentabilidadeDTO;
import com.fleetops.motorista.dto.ResponseDate;
import com.fleetops.motorista.service.CardsCustoService;
import com.fleetops.motorista.service.ServicoManifesto;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private CardsCustoService cardsCustoService;

    @GetMapping
    public List<ManifestoDTO> listarTodos() {
        return servico.listarTodos().stream().map(ManifestoDTO::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManifestoDTO> buscarPorId(@PathVariable Long id) {
        return servico.buscarPorId(id)
                .map(ManifestoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/dataDoUltimoManifesto")
    public ResponseEntity<ResponseDate> buscarDataUltimoManifesto() {
        return servico.buscarDataUltimoManifesto()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rentabilidade/{id}/{mes}/{ano}")
    public ResponseEntity<RentabilidadeDTO> rentabilidadeMediaPorIdMotorista(
            @PathVariable Long id, @PathVariable Long mes, @PathVariable Long ano) {
        RentabilidadeDTO rentabilidade = servico.RentabilidademanifestosPorMotoristaEAnoEMes(id, mes, ano);

        return ResponseEntity.ok(rentabilidade);
    }

    @GetMapping("/rentabilidadeGeral/{inicio}/{fim}")
    public ResponseEntity<HashMap<Integer, RentabilidadeDTO>> rentabilidadeGeralDaEmpresa(
            @PathVariable LocalDate inicio, @PathVariable LocalDate fim) {
        HashMap<Integer, RentabilidadeDTO> dadosDeAcordoComAsDatas = servico.RentabilidadeNoUltimosMeses(inicio, fim);
        return ResponseEntity.ok(dadosDeAcordoComAsDatas);
    }

    @GetMapping("/rentabilidadeGrandeSp/{id}/{mes}/{ano}")
    public ResponseEntity<RentabilidadeDTO> rentabilidadeMediaPorIdMotoristaGrandeSP(
            @PathVariable Long id, @PathVariable Long mes, @PathVariable Long ano) {
        RentabilidadeDTO rentabilidade = servico.RentabilidademanifestosPorMotoristaEAnoEMesGrandeSP(id, mes, ano);

        return ResponseEntity.ok(rentabilidade);
    }

    @GetMapping("/dashboard/cards-custo")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<CardsCustoDTO> cardsCusto(@RequestParam(required = false) String mes) {
        YearMonth ym;
        try {
            ym = mes != null ? YearMonth.parse(mes) : null;
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cardsCustoService.calcular(ym));
    }

    @GetMapping("/rentabilidadeGeralGrandeSp/{inicio}/{fim}")
    public ResponseEntity<HashMap<Integer, RentabilidadeDTO>> rentabilidadeGeralDaEmpresaGrandeSP(
            @PathVariable LocalDate inicio, @PathVariable LocalDate fim) {
        HashMap<Integer, RentabilidadeDTO> dadosDeAcordoComAsDatas =
                servico.RentabilidadeNoUltimosMesesGrandeSp(inicio, fim);
        return ResponseEntity.ok(dadosDeAcordoComAsDatas);
    }
}

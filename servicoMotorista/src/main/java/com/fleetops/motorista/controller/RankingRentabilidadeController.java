package com.fleetops.motorista.controller;

import com.fleetops.motorista.dto.ranking.RankingRentabilidadeResponseDTO;
import com.fleetops.motorista.service.RentabilidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rankings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GERENCIA')")
@Tag(
        name = "Ranking de Rentabilidade",
        description = "Endpoints para consulta do Ranking de Rentabilidade de motoristas (US.14)")
public class RankingRentabilidadeController {

    private final RentabilidadeService rentabilidadeService;

    @GetMapping("/meses-disponiveis")
    @Operation(
            summary = "Lista os meses com manifestos disponíveis no sistema",
            description = "Retorna a lista de meses no formato YYYY-MM ordenada do mais recente para o mais antigo.")
    public ResponseEntity<List<String>> listarMesesDisponiveis() {
        return ResponseEntity.ok(rentabilidadeService.listarMesesDisponiveis());
    }

    @GetMapping("/rentabilidade")
    @Operation(
            summary = "Obtém os dados consolidados do Modal do Ranking de Rentabilidade (US.14)",
            description =
                    "Retorna o resumo do período, ranking completo de motoristas ordenado e os 3 gráficos laterais.")
    public ResponseEntity<RankingRentabilidadeResponseDTO> obterRankingRentabilidade(
            @Parameter(description = "Mês de referência no formato YYYY-MM (padrão: mês mais recente)")
                    @RequestParam(required = false)
                    String mes,
            @Parameter(description = "Visão de análise: GERAL ou GRANDE_SP (padrão: GERAL)")
                    @RequestParam(required = false, defaultValue = "GERAL")
                    String visao) {
        return ResponseEntity.ok(rentabilidadeService.obterRankingModal(mes, visao));
    }
}

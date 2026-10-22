package com.fleetops.avaliacao.controller;

import com.fleetops.avaliacao.dto.AvaliacaoRequestDTO;
import com.fleetops.avaliacao.dto.AvaliacaoResponseDTO;
import com.fleetops.avaliacao.service.AvaliacaoFreteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para registro de avaliações de frete.
 * Rota criada conforme SCRUM-28 e documentada com SpringDoc (SCRUM-48).
 *
 * Endpoint isolado no contexto /api/avaliacoes para facilitar
 * futura extração como microsserviço independente.
 */
@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Avaliações de Frete", description = "Endpoints para registro e consulta de avaliações de motoristas")
public class AvaliacaoFreteController {

    private final AvaliacaoFreteService service;

    @GetMapping
    @Operation(
            summary = "Listar avaliações de frete",
            description = "Retorna a lista de todas as avaliações de frete cadastradas.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Avaliações retornadas com sucesso",
                content =
                        @Content(array = @ArraySchema(schema = @Schema(implementation = AvaliacaoResponseDTO.class)))),
        @ApiResponse(responseCode = "500", description = "Erro interno ao consultar o banco de dados")
    })
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarTodos() {
        return new ResponseEntity<List<AvaliacaoResponseDTO>>(this.service.listarAvaliacoes(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar avaliação de frete por ID",
            description = "Retorna a avaliação de frete correspondente ao ID informado.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Avaliação retornada com sucesso",
                content = @Content(schema = @Schema(implementation = AvaliacaoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada para o ID informado"),
        @ApiResponse(responseCode = "500", description = "Erro interno ao consultar o banco de dados")
    })
    public ResponseEntity<AvaliacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return new ResponseEntity<AvaliacaoResponseDTO>(this.service.buscarAvaliacao(id), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Registrar avaliação de frete",
            description =
                    """
            Recebe nota (1 a 5), número do manifesto e feedback obrigatórios, vinculando-os ao motorista informado.
            Escala: 1=Péssimo, 2=Ruim, 3=Regular, 4=Bom, 5=Excelente.
            A avaliação fica associada ao motorista e à data de registro para uso futuro no ranking.
            """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Avaliação registrada com sucesso",
                content = @Content(schema = @Schema(implementation = AvaliacaoResponseDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos (nota fora de 1-5, campos obrigatórios ausentes)"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AvaliacaoResponseDTO> registrar(@Valid @RequestBody AvaliacaoRequestDTO request) {
        AvaliacaoResponseDTO response = service.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

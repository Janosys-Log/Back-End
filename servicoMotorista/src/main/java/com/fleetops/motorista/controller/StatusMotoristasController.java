package com.fleetops.motorista.controller;

import com.fleetops.motorista.dto.ContatoMotoristaDTO;
import com.fleetops.motorista.dto.StatusDTO;
import com.fleetops.motorista.service.ServicoContatoMotorista;
import com.fleetops.motorista.service.StatusMotoristasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para registro dos statos do motorista.
 * Rota criada conforme SCRUM-25 e documentada com SpringDoc (SCRUM-46).
 *
 * Endpoint isolado no contexto /status/updateStatus para facilitar
 * futura extração como microsserviço independente.
 */
@RestController
@RequestMapping("/api/status")
@CrossOrigin(origins = "*")
@Tag(name = "Status do Motorista", description = "Endpoints para registro e atualização do status do motorista")
public class StatusMotoristasController {
    private final StatusMotoristasService motoristaService;
    private final ServicoContatoMotorista servicoContato;

    StatusMotoristasController(StatusMotoristasService service, ServicoContatoMotorista servicoContato) {
        this.motoristaService = service;
        this.servicoContato = servicoContato;
    }

    @PutMapping("/updateStatus")
    @Operation(
            summary = "Atualizar status do motorista",
            description =
                    """
            Recebe status (Disponivel, Em_Rota, Indisponivel, Indesejado) e motoristaId obrigatórios.
            Para colocar o motorista em rota (Em_Rota) também é obrigatório informar numeroManifesto (inteiro positivo),
            que não pode estar em uso por outro motorista em rota. Nos demais status o numeroManifesto é ignorado
            e o manifesto atual do motorista é limpo.
            Fluxo:
              Motorista Com Status Disponível → Operador Realiza Ligação →  Motorista aceita o Frete → Status Muda para “Em Rota” (informando o manifesto)
              Motorista Com Status Disponível → Operador Realiza Ligação → Motorista Recusa → Status muda para Indisponível (Status dura 15 dias ou até o operador alterar)
            """)
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Status atualizado com sucesso"),
        @ApiResponse(
                responseCode = "400",
                description =
                        "Fluxo de status incorreto, manifesto ausente ou inválido para Em_Rota, ou corpo da requisição inválido"),
        @ApiResponse(responseCode = "404", description = "Motorista não encontrado"),
        @ApiResponse(responseCode = "409", description = "Manifesto já está em uso por outro motorista em rota")
    })
    public ResponseEntity<?> atualizaStatusMotorista(
            @Valid @RequestBody StatusDTO statusDTO, Authentication authentication) {
        if (authentication != null && statusDTO.getMotoristaId() != null) {
            Optional<ContatoMotoristaDTO> contato = servicoContato.obterContatoAtivo(statusDTO.getMotoristaId());
            if (contato.isPresent() && !contato.get().email().equalsIgnoreCase(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "erro",
                                "Motorista em contato com " + contato.get().nome() + " — aguarde a liberação."));
            }
        }
        motoristaService.atualizaStatusMotorista(statusDTO);
        return ResponseEntity.noContent().build();
    }
}

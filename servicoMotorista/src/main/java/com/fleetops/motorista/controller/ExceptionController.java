package com.fleetops.motorista.controller;

import com.fleetops.motorista.dto.ResponseErrorDTO;
import com.fleetops.motorista.exceptions.FluxoStatusNaoEsperadoException;
import com.fleetops.motorista.exceptions.ManifestoEmUsoException;
import com.fleetops.motorista.exceptions.ManifestoObrigatorioException;
import com.fleetops.motorista.exceptions.MotoristaNaoEncontradoException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handler global de exceções para retornar mensagens claras de validação.
 */
@ControllerAdvice
public class ExceptionController {
    private static final String MENSAGEM_CORPO_INVALIDO =
            "Corpo da requisição inválido. Verifique se os campos têm o formato esperado";
    private static final String MENSAGEM_DADOS_INVALIDOS = "Dados da requisição inválidos";

    @ExceptionHandler(FluxoStatusNaoEsperadoException.class)
    public ResponseEntity<ResponseErrorDTO> handleClienteInvalidoException(FluxoStatusNaoEsperadoException ex) {
        ResponseErrorDTO erroResposta = new ResponseErrorDTO(ex.getMessage());
        return new ResponseEntity<ResponseErrorDTO>(erroResposta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MotoristaNaoEncontradoException.class)
    public ResponseEntity<ResponseErrorDTO> handleMotoristaNaoEncontradoException(MotoristaNaoEncontradoException ex) {
        ResponseErrorDTO erroResposta = new ResponseErrorDTO(ex.getMessage());
        return new ResponseEntity<ResponseErrorDTO>(erroResposta, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ManifestoObrigatorioException.class)
    public ResponseEntity<ResponseErrorDTO> handleManifestoObrigatorioException(ManifestoObrigatorioException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ManifestoEmUsoException.class)
    public ResponseEntity<ResponseErrorDTO> handleManifestoEmUsoException(ManifestoEmUsoException ex) {
        return montarResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorDTO> handleCampoInvalidoException(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(MENSAGEM_DADOS_INVALIDOS);
        return montarResposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseErrorDTO> handleCorpoIlegivelException(HttpMessageNotReadableException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, MENSAGEM_CORPO_INVALIDO);
    }

    private ResponseEntity<ResponseErrorDTO> montarResposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ResponseErrorDTO(mensagem));
    }
}

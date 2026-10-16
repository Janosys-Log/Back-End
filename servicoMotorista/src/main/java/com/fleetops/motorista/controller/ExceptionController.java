package com.fleetops.motorista.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fleetops.motorista.dto.ResponseErrorDTO;
import com.fleetops.motorista.exceptions.FluxoStatusNaoEsperadoException;
import com.fleetops.motorista.exceptions.MotoristaNaoEncontradoException;

/**
 * Handler global de exceções para retornar mensagens claras de validação.
 */
@ControllerAdvice
public class ExceptionController {
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
}

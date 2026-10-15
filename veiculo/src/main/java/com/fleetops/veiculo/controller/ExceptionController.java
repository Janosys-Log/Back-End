package com.fleetops.veiculo.controller;

import com.fleetops.veiculo.dto.ResponseErrorDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorDTO> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Dados inválidos");
        return ResponseEntity.badRequest().body(new ResponseErrorDTO(msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseErrorDTO> handleConstraint(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(new ResponseErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseErrorDTO> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(
                        new ResponseErrorDTO(
                                "Formato de requisição inválido ou valor de Tipo/Subtipo não reconhecido. Verifique os valores aceitos."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseErrorDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ResponseErrorDTO(
                                "Violação de integridade dos dados (ex: placa de veículo já cadastrada ou chave estrangeira inexistente)."));
    }
}

package com.fleetops.usuario.exception;

import com.fleetops.usuario.dto.ErrorResponseDTO;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UsuarioExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> regraNegocio(RegraNegocioException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErrorResponseDTO> conflito(ConflitoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> naoEncontrado(NaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> validacao(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(mensagem));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> corpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO("Corpo da requisição inválido (confira perfil e papel)."));
    }
}

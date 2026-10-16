package com.fleetops.motorista.service;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.dto.StatusDTO;
import com.fleetops.motorista.exceptions.FluxoStatusNaoEsperadoException;
import com.fleetops.motorista.exceptions.MotoristaNaoEncontradoException;
import com.fleetops.motorista.repository.MotoristaRepository;

/**
 * Serviço do status do motorista.
 * Contém a lógica de negócio e permanece isolado no pacote statusMotorista
 * para facilitar futura migração para microsserviço.
 */
@Service
public class StatusMotoristasService {
    private final MotoristaRepository motoristaRepository;

    StatusMotoristasService(MotoristaRepository repository) {
        this.motoristaRepository = repository;
    }

    public ResponseEntity<String> atualizaStatusMotorista(StatusDTO statusDTO) {
        Optional<Motorista> motoristaOpt = this.motoristaRepository.findById(statusDTO.getMotoristaId());
        Motorista motorista =
                motoristaOpt.orElseThrow(() -> new MotoristaNaoEncontradoException(statusDTO.getMotoristaId()));

        if (motorista.getStatus() == StatusMotorista.Disponivel
                && (statusDTO.getStatus() != StatusMotorista.Em_Rota
                        || statusDTO.getStatus() != StatusMotorista.Indisponivel))
            throw new FluxoStatusNaoEsperadoException(statusDTO.getStatus());

        motorista.atualizaStatus(statusDTO.getStatus());

        return new ResponseEntity<String>("Status alterado com sucesso", HttpStatus.NO_CONTENT);
    }
}

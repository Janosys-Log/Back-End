package com.fleetops.servicoMotorista.service;

import com.fleetops.servicoMotorista.domain.Motorista;
import com.fleetops.servicoMotorista.domain.StatusMotorista;
import com.fleetops.servicoMotorista.dto.StatusDTO;
import com.fleetops.servicoMotorista.exceptions.FluxoStatusNaoEsperadoException;
import com.fleetops.servicoMotorista.exceptions.MotoristaNaoEncontradoException;
import com.fleetops.servicoMotorista.repository.MotoristaRepository;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
                        && statusDTO.getStatus() != StatusMotorista.Indisponivel
                        && statusDTO.getStatus() != StatusMotorista.Indesejado))
            throw new FluxoStatusNaoEsperadoException(statusDTO.getStatus());

        motorista.atualizaStatus(statusDTO.getStatus());
        motoristaRepository.save(motorista);

        return new ResponseEntity<String>("Status alterado com sucesso", HttpStatus.NO_CONTENT);
    }
}

package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.dto.StatusDTO;
import com.fleetops.motorista.exceptions.FluxoStatusNaoEsperadoException;
import com.fleetops.motorista.exceptions.ManifestoEmUsoException;
import com.fleetops.motorista.exceptions.ManifestoObrigatorioException;
import com.fleetops.motorista.exceptions.MotoristaNaoEncontradoException;
import com.fleetops.motorista.repository.MotoristaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusMotoristasService {
    private final MotoristaRepository motoristaRepository;

    public void atualizaStatusMotorista(StatusDTO statusDTO) {
        Motorista motorista = buscarMotorista(statusDTO.getMotoristaId());
        StatusMotorista novoStatus = statusDTO.getStatus();

        validarFluxo(motorista, novoStatus);

        if (novoStatus == StatusMotorista.Em_Rota) {
            colocarEmRota(motorista, statusDTO.getNumeroManifesto());
        } else {
            motorista.atualizaStatus(novoStatus);
        }

        motoristaRepository.save(motorista);
    }

    private Motorista buscarMotorista(Long idMotorista) {
        return motoristaRepository
                .findById(idMotorista)
                .orElseThrow(() -> new MotoristaNaoEncontradoException(idMotorista));
    }

    private void validarFluxo(Motorista motorista, StatusMotorista novoStatus) {
        StatusMotorista atual = motorista.getStatus();

        // Já disponível e tenta marcar disponível de novo
        if (atual == StatusMotorista.Disponivel && novoStatus == StatusMotorista.Disponivel) {
            throw new FluxoStatusNaoEsperadoException(novoStatus);
        }

        // Em rota: só pode concluir (ir para Disponível). Indisponível/Indesejado bloqueados.
        if (atual == StatusMotorista.Em_Rota && novoStatus != StatusMotorista.Disponivel) {
            throw new FluxoStatusNaoEsperadoException(novoStatus);
        }
    }

    private void colocarEmRota(Motorista motorista, Long numeroManifesto) {
        validarManifestoParaEntrarEmRota(motorista, numeroManifesto);
        motorista.iniciarRota(numeroManifesto);
    }

    private void validarManifestoParaEntrarEmRota(Motorista motorista, Long numeroManifesto) {
        if (numeroManifesto == null) {
            throw new ManifestoObrigatorioException();
        }
        if (manifestoEstaEmUsoPorOutroMotorista(motorista, numeroManifesto)) {
            throw new ManifestoEmUsoException(numeroManifesto);
        }
    }

    private boolean manifestoEstaEmUsoPorOutroMotorista(Motorista motorista, Long numeroManifesto) {
        return motoristaRepository.existsByManifestoAtualAndIdMotoristaNot(numeroManifesto, motorista.getIdMotorista());
    }
}

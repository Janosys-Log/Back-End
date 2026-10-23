package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.repository.MotoristaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndisponivelScheduler {

    private final MotoristaRepository motoristaRepository;

    @Value("${motorista.indisponivel.dias:3}")
    private int diasIndisponivel;

    public IndisponivelScheduler(MotoristaRepository motoristaRepository) {
        this.motoristaRepository = motoristaRepository;
    }

    @Scheduled(fixedRate = 360000)
    @Transactional
    public void reativarIndisponiveis() {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasIndisponivel);
        List<Motorista> antigos = motoristaRepository.findIndisponiveisAntigos(StatusMotorista.Indesejado, limite);
        for (Motorista m : antigos) {
            m.atualizaStatus(StatusMotorista.Disponivel);
            motoristaRepository.save(m);
        }
    }
}

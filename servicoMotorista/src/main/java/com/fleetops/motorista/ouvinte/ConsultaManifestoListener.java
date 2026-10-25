package com.fleetops.motorista.ouvinte;

import com.fleetops.motorista.config.RabbitConfig;
import com.fleetops.motorista.repository.ManifestoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultaManifestoListener {
    private final ManifestoRepositorio manifestoRepositorio;

    @RabbitListener(queues = RabbitConfig.filaConsultaManifesto)
    public boolean existe(Long manifestoId) {
        return manifestoRepositorio.existsById(manifestoId);
    }
}

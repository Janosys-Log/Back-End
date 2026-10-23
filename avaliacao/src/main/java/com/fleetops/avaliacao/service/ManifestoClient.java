package com.fleetops.avaliacao.service;

import com.fleetops.avaliacao.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ManifestoClient {
    private final RabbitTemplate rabbitTemplate;

    public boolean existe(Long manifestoId) {
        Boolean resposta;

        try {
            resposta = rabbitTemplate.convertSendAndReceiveAsType(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.ROUTING_KEY,
                    manifestoId,
                    new ParameterizedTypeReference<Boolean>() {});
        } catch (AmqpException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Falha na comunicação ao consultar o manifesto", ex);
        }

        if (resposta == null) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Não foi recebida uma resposta sobre o manifesto");
        }

        return resposta;
    }
}

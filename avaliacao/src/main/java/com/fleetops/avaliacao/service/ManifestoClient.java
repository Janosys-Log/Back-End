package com.fleetops.avaliacao.service;

import org.springframework.stereotype.Service;

/**
 * Cliente legado de consulta de manifesto via RabbitMQ.
 * A partir da US12+ a avaliação NÃO exige que o manifesto exista na base:
 * o número digitado no modal é gravado em num_modal sem checagem.
 * Mantido apenas para não quebrar eventuais injeções; existe() sempre retorna true.
 */
@Service
public class ManifestoClient {

    /** Sempre true — não consulta banco/RabbitMQ. */
    public boolean existe(Long manifestoId) {
        return true;
    }
}

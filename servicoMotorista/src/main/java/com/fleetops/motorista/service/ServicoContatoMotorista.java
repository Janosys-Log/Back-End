package com.fleetops.motorista.service;

import com.fleetops.motorista.dto.ContatoMotoristaDTO;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Service;

@Service
public class ServicoContatoMotorista {
    public static final Duration TTL = Duration.ofMinutes(2);

    private final ConcurrentHashMap<Long, ContatoMotoristaDTO> contatos = new ConcurrentHashMap<>();

    public record ResultadoLock(boolean adquirido, ContatoMotoristaDTO contato) {}

    public Map<Long, ContatoMotoristaDTO> listarContatosAtivos() {
        Instant agora = Instant.now();
        Map<Long, ContatoMotoristaDTO> ativos = new HashMap<>();
        contatos.forEach((id, contato) -> {
            if (contato.expiraEm().isAfter(agora)) {
                ativos.put(id, contato);
            } else {
                contatos.remove(id, contato);
            }
        });
        return ativos;
    }

    public Optional<ContatoMotoristaDTO> obterContatoAtivo(Long motoristaId) {
        if (motoristaId == null) {
            return Optional.empty();
        }
        ContatoMotoristaDTO contato = contatos.get(motoristaId);
        if (contato == null) {
            return Optional.empty();
        }
        if (!contato.expiraEm().isAfter(Instant.now())) {
            contatos.remove(motoristaId, contato);
            return Optional.empty();
        }
        return Optional.of(contato);
    }

    public ResultadoLock adquirirOuRenovar(Long motoristaId, String email, String nome) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(TTL);
        String nomeExibicao = (nome != null && !nome.isBlank()) ? nome : email;
        AtomicBoolean adquirido = new AtomicBoolean(false);

        ContatoMotoristaDTO resultante = contatos.compute(motoristaId, (id, atual) -> {
            if (atual == null
                    || !atual.expiraEm().isAfter(agora)
                    || atual.email().equalsIgnoreCase(email)) {
                adquirido.set(true);
                return new ContatoMotoristaDTO(id, email, nomeExibicao, expiraEm);
            }
            return atual;
        });

        if (adquirido.get()) {
            contatos.forEach((outroId, outroContato) -> {
                if (!outroId.equals(motoristaId) && outroContato.email().equalsIgnoreCase(email)) {
                    contatos.remove(outroId, outroContato);
                }
            });
        }

        return new ResultadoLock(adquirido.get(), resultante);
    }

    public void liberar(Long motoristaId, String email) {
        if (motoristaId == null || email == null) {
            return;
        }
        contatos.computeIfPresent(motoristaId, (id, atual) -> atual.email().equalsIgnoreCase(email) ? null : atual);
    }

    public void liberarTodosDoUsuario(String email) {
        if (email == null) {
            return;
        }
        contatos.forEach((id, atual) -> {
            if (atual.email().equalsIgnoreCase(email)) {
                contatos.remove(id, atual);
            }
        });
    }
}

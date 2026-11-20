package com.fleetops.motorista.dto;

import java.time.Instant;

public record ContatoMotoristaDTO(Long motoristaId, String email, String nome, Instant expiraEm) {}

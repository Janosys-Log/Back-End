package com.fleetops.motorista.dto.ranking;

import java.util.List;

public record PaginaNegativosResponseDTO(
        ResumoNegativosDTO resumo, List<MotoristaNegativoItemDTO> motoristas, List<String> mesesDisponiveis) {}

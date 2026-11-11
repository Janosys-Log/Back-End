package com.fleetops.motorista.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fleetops.motorista.domain.StatusMotorista;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

class StatusDTOTest {
    private static final Validator validador =
            Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -987654})
    void deveRecusarNumeroManifestoMenorQueUm(long numeroManifesto) {
        StatusDTO pedido = pedido(numeroManifesto);

        Set<ConstraintViolation<StatusDTO>> violacoes = validador.validate(pedido);

        assertEquals(1, violacoes.size());
        assertEquals(
                "O número do manifesto deve ser maior que zero",
                violacoes.iterator().next().getMessage());
    }

    @Test
    void deveAceitarNumeroManifestoPositivo() {
        StatusDTO pedido = pedido(123456L);

        assertTrue(validador.validate(pedido).isEmpty());
    }

    @Test
    void deveAceitarNumeroManifestoAusenteNaValidacaoDoCampo() {
        StatusDTO pedido = pedido(null);

        assertTrue(validador.validate(pedido).isEmpty());
    }

    private static StatusDTO pedido(Long numeroManifesto) {
        StatusDTO pedido = new StatusDTO();
        ReflectionTestUtils.setField(pedido, "motoristaId", 1L);
        ReflectionTestUtils.setField(pedido, "status", StatusMotorista.Em_Rota);
        ReflectionTestUtils.setField(pedido, "numeroManifesto", numeroManifesto);
        return pedido;
    }
}

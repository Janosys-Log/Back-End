package com.fleetops.motorista.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.dto.StatusDTO;
import com.fleetops.motorista.exceptions.FluxoStatusNaoEsperadoException;
import com.fleetops.motorista.exceptions.ManifestoEmUsoException;
import com.fleetops.motorista.exceptions.ManifestoObrigatorioException;
import com.fleetops.motorista.exceptions.MotoristaNaoEncontradoException;
import com.fleetops.motorista.repository.MotoristaRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StatusMotoristasServiceTest {
    private static final Long ID_MOTORISTA = 1L;
    private static final Long NUMERO_MANIFESTO = 123456L;

    @Mock
    private MotoristaRepository motoristaRepository;

    @InjectMocks
    private StatusMotoristasService service;

    @Test
    void deveRecusarEntradaEmRotaQuandoManifestoNaoForInformado() {
        Motorista motorista = motoristaDisponivel();
        StatusDTO pedido = pedido(StatusMotorista.Em_Rota, null);
        when(motoristaRepository.findById(ID_MOTORISTA)).thenReturn(Optional.of(motorista));

        assertThrows(ManifestoObrigatorioException.class, () -> service.atualizaStatusMotorista(pedido));

        verify(motoristaRepository, never()).save(any());
    }

    @Test
    void deveRecusarEntradaEmRotaQuandoManifestoJaEstaEmUsoPorOutroMotorista() {
        Motorista motorista = motoristaDisponivel();
        StatusDTO pedido = pedido(StatusMotorista.Em_Rota, NUMERO_MANIFESTO);
        when(motoristaRepository.findById(ID_MOTORISTA)).thenReturn(Optional.of(motorista));
        when(motoristaRepository.existsByManifestoAtualAndIdMotoristaNot(NUMERO_MANIFESTO, ID_MOTORISTA))
                .thenReturn(true);

        assertThrows(ManifestoEmUsoException.class, () -> service.atualizaStatusMotorista(pedido));

        verify(motoristaRepository, never()).save(any());
    }

    @Test
    void deveColocarMotoristaEmRotaGravandoOManifestoInformado() {
        Motorista motorista = motoristaDisponivel();
        StatusDTO pedido = pedido(StatusMotorista.Em_Rota, NUMERO_MANIFESTO);
        when(motoristaRepository.findById(ID_MOTORISTA)).thenReturn(Optional.of(motorista));
        when(motoristaRepository.existsByManifestoAtualAndIdMotoristaNot(NUMERO_MANIFESTO, ID_MOTORISTA))
                .thenReturn(false);

        service.atualizaStatusMotorista(pedido);

        assertEquals(StatusMotorista.Em_Rota, motorista.getStatus());
        assertEquals(NUMERO_MANIFESTO, motorista.getManifestoAtual());
        verify(motoristaRepository).save(motorista);
    }

    @Test
    void deveLimparManifestoAoConcluirRotaVoltandoParaDisponivel() {
        Motorista motorista = motoristaEmRota();
        StatusDTO pedido = pedido(StatusMotorista.Disponivel, null);
        when(motoristaRepository.findById(ID_MOTORISTA)).thenReturn(Optional.of(motorista));

        service.atualizaStatusMotorista(pedido);

        assertEquals(StatusMotorista.Disponivel, motorista.getStatus());
        assertNull(motorista.getManifestoAtual());
        verify(motoristaRepository).save(motorista);
    }

    @ParameterizedTest
    @EnumSource(
            value = StatusMotorista.class,
            names = {"Indisponivel", "Indesejado"})
    void deveLimparManifestoAoSairDeEmRotaParaStatusDeRestricao(StatusMotorista novoStatus) {
        Motorista motorista = motoristaEmRota();
        StatusDTO pedido = pedido(novoStatus, null);
        when(motoristaRepository.findById(ID_MOTORISTA)).thenReturn(Optional.of(motorista));

        service.atualizaStatusMotorista(pedido);

        assertEquals(novoStatus, motorista.getStatus());
        assertNull(motorista.getManifestoAtual());
    }

    @Test
    void deveIgnorarManifestoInformadoComStatusDiferenteDeEmRota() {
        Motorista motorista = motoristaDisponivel();
        StatusDTO pedido = pedido(StatusMotorista.Indisponivel, NUMERO_MANIFESTO);
        when(motoristaRepository.findById(ID_MOTORISTA)).thenReturn(Optional.of(motorista));

        service.atualizaStatusMotorista(pedido);

        assertEquals(StatusMotorista.Indisponivel, motorista.getStatus());
        assertNull(motorista.getManifestoAtual());
        verify(motoristaRepository, never()).existsByManifestoAtualAndIdMotoristaNot(any(), any());
    }

    @Test
    void deveRecusarMudancaDeDisponivelParaDisponivel() {
        Motorista motorista = motoristaDisponivel();
        StatusDTO pedido = pedido(StatusMotorista.Disponivel, null);
        when(motoristaRepository.findById(ID_MOTORISTA)).thenReturn(Optional.of(motorista));

        assertThrows(FluxoStatusNaoEsperadoException.class, () -> service.atualizaStatusMotorista(pedido));

        verify(motoristaRepository, never()).save(any());
    }

    @Test
    void deveRecusarAtualizacaoQuandoMotoristaNaoExiste() {
        StatusDTO pedido = pedido(StatusMotorista.Em_Rota, NUMERO_MANIFESTO);
        when(motoristaRepository.findById(ID_MOTORISTA)).thenReturn(Optional.empty());

        assertThrows(MotoristaNaoEncontradoException.class, () -> service.atualizaStatusMotorista(pedido));

        verify(motoristaRepository, never()).save(any());
    }

    private static Motorista motoristaDisponivel() {
        return Motorista.builder()
                .idMotorista(ID_MOTORISTA)
                .nomeMotorista("Marcos Vieira")
                .build();
    }

    private static Motorista motoristaEmRota() {
        Motorista motorista = motoristaDisponivel();
        motorista.iniciarRota(NUMERO_MANIFESTO);
        return motorista;
    }

    private static StatusDTO pedido(StatusMotorista status, Long numeroManifesto) {
        StatusDTO pedido = new StatusDTO();
        ReflectionTestUtils.setField(pedido, "motoristaId", ID_MOTORISTA);
        ReflectionTestUtils.setField(pedido, "status", status);
        ReflectionTestUtils.setField(pedido, "numeroManifesto", numeroManifesto);
        return pedido;
    }
}

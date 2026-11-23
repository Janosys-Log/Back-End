package com.fleetops.CsvParaJson.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fleetops.CsvParaJson.config.RabbitConfig;
import com.fleetops.CsvParaJson.dto.response.ManifestoDTO;
import com.fleetops.CsvParaJson.dto.response.ResultadoCsv;
import com.fleetops.CsvParaJson.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.CsvParaJson.dto.response.VeiculoDTO;
import com.fleetops.CsvParaJson.exceptions.tipos.ArquivoInexistente;
import com.fleetops.CsvParaJson.service.CsvParaJson;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class CsvVeiculosControllerTest {

    @Mock
    private CsvParaJson<VeiculoDTO> csvParaVeiculo;

    @Mock
    private CsvParaJson<ManifestoDTO> csvParaManifesto;

    @InjectMocks
    private CsvVeiculosController controller;

    private MockMultipartFile arquivoVeiculo;
    private MockMultipartFile arquivoManifesto;

    @BeforeEach
    void setUp() {
        arquivoVeiculo = new MockMultipartFile("csvVeiculo", "veiculos.csv", "text/csv", "placa;modelo".getBytes());
        arquivoManifesto =
                new MockMultipartFile("csvManifesto", "manifestos.csv", "text/csv", "manifesto;data".getBytes());
    }

    @Test
    void deveImportarApenasVeiculosComSucesso() {
        ResultadoLinhasDoCsv<VeiculoDTO> resVeiculo =
                new ResultadoLinhasDoCsv<>(Collections.emptyList(), Collections.emptyList());
        when(csvParaVeiculo.RetornarJson(
                        eq(arquivoVeiculo), eq(VeiculoDTO.class), eq(RabbitConfig.routing_key_veiculosJson)))
                .thenReturn(resVeiculo);

        ResultadoCsv<VeiculoDTO, ManifestoDTO> resultado = controller.csvParaJsonVeiculo(arquivoVeiculo, null);

        assertNotNull(resultado);
        assertNotNull(resultado.getResultadoLinhasDoCsv());
        assertNotNull(resultado.getVeiculos());
        assertNull(resultado.getResultadoLinhasDoCsv2());
        assertNull(resultado.getManifestos());
        verify(csvParaVeiculo, times(1)).RetornarJson(any(), any(), any());
        verifyNoInteractions(csvParaManifesto);
    }

    @Test
    void deveImportarApenasManifestosComSucesso() {
        ResultadoLinhasDoCsv<ManifestoDTO> resManifesto =
                new ResultadoLinhasDoCsv<>(Collections.emptyList(), Collections.emptyList());
        when(csvParaManifesto.RetornarJson(
                        eq(arquivoManifesto), eq(ManifestoDTO.class), eq(RabbitConfig.routing_key_ManifestoJson)))
                .thenReturn(resManifesto);

        ResultadoCsv<VeiculoDTO, ManifestoDTO> resultado = controller.csvParaJsonVeiculo(null, arquivoManifesto);

        assertNotNull(resultado);
        assertNull(resultado.getResultadoLinhasDoCsv());
        assertNull(resultado.getVeiculos());
        assertNotNull(resultado.getResultadoLinhasDoCsv2());
        assertNotNull(resultado.getManifestos());
        verify(csvParaManifesto, times(1)).RetornarJson(any(), any(), any());
        verifyNoInteractions(csvParaVeiculo);
    }

    @Test
    void deveImportarAmbosComSucesso() {
        ResultadoLinhasDoCsv<VeiculoDTO> resVeiculo =
                new ResultadoLinhasDoCsv<>(Collections.emptyList(), Collections.emptyList());
        ResultadoLinhasDoCsv<ManifestoDTO> resManifesto =
                new ResultadoLinhasDoCsv<>(Collections.emptyList(), Collections.emptyList());
        when(csvParaVeiculo.RetornarJson(
                        eq(arquivoVeiculo), eq(VeiculoDTO.class), eq(RabbitConfig.routing_key_veiculosJson)))
                .thenReturn(resVeiculo);
        when(csvParaManifesto.RetornarJson(
                        eq(arquivoManifesto), eq(ManifestoDTO.class), eq(RabbitConfig.routing_key_ManifestoJson)))
                .thenReturn(resManifesto);

        ResultadoCsv<VeiculoDTO, ManifestoDTO> resultado =
                controller.csvParaJsonVeiculo(arquivoVeiculo, arquivoManifesto);

        assertNotNull(resultado);
        assertNotNull(resultado.getResultadoLinhasDoCsv());
        assertNotNull(resultado.getResultadoLinhasDoCsv2());
        verify(csvParaVeiculo, times(1)).RetornarJson(any(), any(), any());
        verify(csvParaManifesto, times(1)).RetornarJson(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoNenhumArquivoForEnviado() {
        assertThrows(ArquivoInexistente.class, () -> controller.csvParaJsonVeiculo(null, null));

        MockMultipartFile vazio1 = new MockMultipartFile("csvVeiculo", "", "text/csv", new byte[0]);
        MockMultipartFile vazio2 = new MockMultipartFile("csvManifesto", "", "text/csv", new byte[0]);
        assertThrows(ArquivoInexistente.class, () -> controller.csvParaJsonVeiculo(vazio1, vazio2));
    }
}

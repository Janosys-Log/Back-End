package com.fleetops.motorista.ouvinte;

import com.fleetops.motorista.config.RabbitConfig;
import com.fleetops.motorista.ouvinte.dto.response.ManifestoDTO;
import com.fleetops.motorista.ouvinte.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.motorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.motorista.service.ServicoAgregado;
import com.fleetops.motorista.service.ServicoManifesto;
import com.fleetops.motorista.service.ServicoMotorista;
import com.fleetops.motorista.service.ServicoRegistroImportacao;
import com.fleetops.motorista.service.ServicoVeiculo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OuvinteCsvParaJson {
    @Autowired
    private ServicoVeiculo servicoVeiculo;

    @Autowired
    private ServicoAgregado servicoAgregado;

    @Autowired
    private ServicoMotorista servicoMotorista;

    @Autowired
    private ServicoManifesto servicoManifesto;

    @Autowired
    private ServicoRegistroImportacao servicoRegistroImportacao;

    @Transactional
    @RabbitListener(queues = RabbitConfig.filaVeiculo)
    public boolean salvarDadosRebidos(ResultadoLinhasDoCsv<VeiculoDTO> JsonCsv) {
        if (JsonCsv != null && JsonCsv.getValidos() != null) {
            for (VeiculoDTO veiculo : JsonCsv.getValidos()) {
                try {
                    servicoAgregado.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
                    servicoVeiculo.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
                    servicoMotorista.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
                } catch (Exception e) {
                    System.err.println(
                            "Erro ao processar linha de veículo (" + veiculo.getPlaca() + "): " + e.getMessage());
                }
            }
        }
        try {
            servicoRegistroImportacao.registrarImportacao(
                    "VEICULOS",
                    JsonCsv != null ? JsonCsv.getNomeArquivo() : null,
                    JsonCsv != null ? JsonCsv.getDataHora() : null,
                    (JsonCsv != null && JsonCsv.getValidos() != null)
                            ? JsonCsv.getValidos().size()
                            : 0);
        } catch (Exception e) {
            System.err.println("Erro ao registrar importação de veículos: " + e.getMessage());
        }
        System.out.println("Arquivo de Veículos Gravado com sucesso");
        return true;
    }

    @RabbitListener(queues = RabbitConfig.filaManifesto)
    public boolean salvarDadosRebidosManifesto(ResultadoLinhasDoCsv<ManifestoDTO> Jsoncsv) {
        if (Jsoncsv != null
                && Jsoncsv.getValidos() != null
                && !Jsoncsv.getValidos().isEmpty()) {
            servicoManifesto.salvarManifestosEmLote(Jsoncsv.getValidos());
        }
        try {
            servicoRegistroImportacao.registrarImportacao(
                    "MANIFESTOS",
                    Jsoncsv != null ? Jsoncsv.getNomeArquivo() : null,
                    Jsoncsv != null ? Jsoncsv.getDataHora() : null,
                    (Jsoncsv != null && Jsoncsv.getValidos() != null)
                            ? Jsoncsv.getValidos().size()
                            : 0);
        } catch (Exception e) {
            System.err.println("Erro ao registrar importação de manifestos: " + e.getMessage());
        }
        System.out.println("Arquivo de Manifestos Gravado com sucesso");
        return true;
    }
}

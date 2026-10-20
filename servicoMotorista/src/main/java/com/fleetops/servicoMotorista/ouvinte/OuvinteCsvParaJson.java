package com.fleetops.servicoMotorista.ouvinte;

import com.fleetops.servicoMotorista.config.RabbitConfig;
import com.fleetops.servicoMotorista.ouvinte.dto.response.ManifestoDTO;
import com.fleetops.servicoMotorista.ouvinte.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.servicoMotorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.servicoMotorista.service.ServicoAgregado;
import com.fleetops.servicoMotorista.service.ServicoManifesto;
import com.fleetops.servicoMotorista.service.ServicoMotorista;
import com.fleetops.servicoMotorista.service.ServicoVeiculo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @RabbitListener(queues = RabbitConfig.filaVeiculo)
    public boolean salvarDadosRebidos(ResultadoLinhasDoCsv<VeiculoDTO> JsonCsv) {
        for (VeiculoDTO veiculo : JsonCsv.getValidos()) {
            servicoAgregado.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
            servicoVeiculo.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
            servicoMotorista.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
        }
        System.out.println("Arquivo Gravado");
        return true;
    }

    @RabbitListener(queues = RabbitConfig.filaManifesto)
    public boolean salvarDadosRebidosManifesto(ResultadoLinhasDoCsv<ManifestoDTO> Jsoncsv) {
        for (ManifestoDTO manifesto : Jsoncsv.getValidos()) {
            servicoManifesto.SalvaOuNaoNoBancoComAsInformacoesDoManifesto(manifesto);
        }
        System.out.println("Arquivo Gravado");
        return true;
    }
}

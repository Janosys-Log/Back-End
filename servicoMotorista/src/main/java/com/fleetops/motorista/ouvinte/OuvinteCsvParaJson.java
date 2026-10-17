package com.fleetops.motorista.ouvinte;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fleetops.motorista.config.RabbitConfig;
import com.fleetops.motorista.ouvinte.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.motorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.motorista.service.ServicoAgregado;
import com.fleetops.motorista.service.ServicoMotorista;
import com.fleetops.motorista.service.ServicoVeiculo;

@Component 
public class OuvinteCsvParaJson {
     @Autowired 
      private ServicoVeiculo servicoVeiculo;
    
     @Autowired 
     private ServicoAgregado servicoAgregado;

     @Autowired 
     private ServicoMotorista servicoMotorista;
     
    @RabbitListener(queues = RabbitConfig.fila)
    public void salvarDadosRebidos(ResultadoLinhasDoCsv<VeiculoDTO> JsonCsv){
         for(VeiculoDTO veiculo: JsonCsv.getValidos()){
              servicoAgregado.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
              servicoVeiculo.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
              servicoMotorista.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
             
             
         }
    }

}

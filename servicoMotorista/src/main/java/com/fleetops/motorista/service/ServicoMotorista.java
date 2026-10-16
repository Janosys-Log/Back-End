package com.fleetops.motorista.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.Veiculo;
import com.fleetops.motorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.motorista.repository.MotoristaRepository;

@Service 
public class ServicoMotorista {
    @Autowired 
     private MotoristaRepository motoristaRepo;
    
   @Autowired 
   private ServicoAgregado servicoAgregado;

   @Autowired 
   private ServicoVeiculo servicoVeiculo;

     public Motorista pegarPorCPF(String nome){
        return motoristaRepo.findBycpfMotorista(nome);
     }
    
     public void SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(VeiculoDTO csvVeiculo){
          Motorista motoristaBanco=new Motorista();
          if (motoristaRepo.existsByCpfMotorista(csvVeiculo.getCpfmotorista())){
                 motoristaBanco=motoristaRepo.findBycpfMotorista(csvVeiculo.getCpfmotorista());
                 motoristaBanco.setContatoMotorista(csvVeiculo.getContatomotorista());
                 Veiculo veiculo=new Veiculo();
                 veiculo.setAgregado(servicoAgregado.pegarPorCnpj(csvVeiculo.getAgregado()));
                 veiculo.setPlacaVeiculo(csvVeiculo.getPlaca());
                 veiculo.setSubtipoVeiculo(csvVeiculo.getSubtipoVeiculo());
                 veiculo.setTipoVeiculo(csvVeiculo.getTipoVeiculo());
                 if(servicoVeiculo.existe(csvVeiculo.getPlaca())){
                  veiculo=servicoVeiculo.acharPorPlaca(csvVeiculo.getPlaca());
                 }
                 motoristaBanco.setVeiculo(veiculo);
                 motoristaRepo.save(motoristaBanco);
                 return;
                  
          }
          motoristaBanco.setContatoMotorista(csvVeiculo.getContatomotorista());
          motoristaBanco.setCpfMotorista(csvVeiculo.getCpfmotorista());
          motoristaBanco.setNomeMotorista(csvVeiculo.getMotorista());
          motoristaBanco.setAgregadoId( servicoAgregado.pegarPorCnpj(csvVeiculo.getCnpjagregado()));
          motoristaRepo.save(motoristaBanco);
     }
     
}

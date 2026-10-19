package com.fleetops.motorista.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;


import com.fleetops.motorista.domain.Manifesto;
import com.fleetops.motorista.ouvinte.dto.response.ManifestoDTO;
import com.fleetops.motorista.repository.ManifestoRepositorio;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor 
@Service 
public class ServicoManifesto {
    @Autowired 
    private ManifestoRepositorio manifestoRepo;

    @Autowired 
    private ServicoAgregado servAgregado;

    @Autowired 
    private ServicoMotorista servicoMotorista;

    @Autowired 
    private ServicoVeiculo sservicoVeiculo;
    

    public void SalvaOuNaoNoBancoComAsInformacoesDoManifesto(ManifestoDTO manifesto){
         Manifesto manifestoParaSalvar=new Manifesto();
          if(manifestoRepo.existsById(manifesto.getManifestoid())){
              return;
          }

      
        DateTimeFormatter FMT = DateTimeFormatter
        .ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT);

         
        manifestoParaSalvar.setAgregado(servAgregado.pegarPorCnpj(manifesto.getCnpjagregado()));
        manifestoParaSalvar.setDataManifesto(LocalDate.parse(manifesto.getData(),FMT));
        manifestoParaSalvar.setDestino(manifesto.getDestino());
        manifestoParaSalvar.setFreteAgregado(manifesto.getValorfreteagregado());
        manifestoParaSalvar.setFreteEmpresa(manifesto.getValorfreteempresa());
        manifestoParaSalvar.setIdManifesto(manifesto.getManifestoid());
        manifestoParaSalvar.setMotorista(servicoMotorista.pegarPorCPF(manifesto.getCpfmotorista()));
        manifestoParaSalvar.setObservacoes(manifesto.getObservacoes());
        manifestoParaSalvar.setVeiculo(sservicoVeiculo.acharPorPlaca(manifesto.getPlaca()));
        if(manifestoParaSalvar.getAgregado()==null || manifestoParaSalvar.getMotorista()==null || manifestoParaSalvar.getVeiculo()==null){
            return;
        }
        manifestoRepo.save(manifestoParaSalvar);
        
    }
    
}

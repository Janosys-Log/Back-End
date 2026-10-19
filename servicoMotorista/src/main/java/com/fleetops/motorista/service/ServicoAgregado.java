package com.fleetops.motorista.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.motorista.repository.AgregadoRepository;

@Service 
public class ServicoAgregado {
    @Autowired 
    private AgregadoRepository Repo;

    public Agregado pegarPorCnpj(String cnpj){
        return Repo.findBycnpjAgregado(cnpj);
    }
    

    public void SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(VeiculoDTO csvVeiculo){
        try{
        Agregado agregado=new Agregado();
        agregado.setCnpjAgregado(csvVeiculo.getCnpjagregado());
        agregado.setContatoAgregado(csvVeiculo.getContatoagregado());
        agregado.setNomeAgregado(csvVeiculo.getAgregado());
        if(Repo.existsBycnpjAgregado(csvVeiculo.getCnpjagregado())){
            agregado=Repo.findBycnpjAgregado(csvVeiculo.getCnpjagregado());
            agregado.setContatoAgregado(csvVeiculo.getContatoagregado());
            Repo.save(agregado);
            return;
        }
        Repo.save(agregado);

    }catch(Exception  e){
        System.out.println("Não foi possivel realizar o salvamento desse Agregado");
        System.out.println("Informações desse agregado:");
        System.out.println("Nome:"+csvVeiculo.getAgregado());
        System.out.println("Cnpj:"+csvVeiculo.getCnpjagregado());
        System.out.println("Contato:"+csvVeiculo.getContatoagregado());
    }
    }

}

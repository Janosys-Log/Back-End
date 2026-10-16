package com.fleetops.motorista.service;

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
    }

}

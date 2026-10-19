package com.fleetops.motorista.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.domain.Veiculo;
import com.fleetops.motorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.motorista.repository.VeiculoRepository;

@Service 
public class ServicoVeiculo {
    @Autowired 
    private VeiculoRepository Repo;

    @Autowired 
    private ServicoAgregado servicoagregado;
   

    public boolean existe(String Placa){
         boolean existe= Repo.existsByplacaVeiculo(Placa);
         return existe;
    }

    public void SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(VeiculoDTO veiculo){
        try{
            Veiculo veiculoparasalvar=new Veiculo();
            if(existe(veiculo.getPlaca())){
             veiculoparasalvar=Repo.findByplacaVeiculo(veiculo.getPlaca());
             veiculoparasalvar.setTipoVeiculo(veiculo.getSubtipoVeiculo().getTipo());
            veiculoparasalvar.setSubtipoVeiculo(veiculo.getSubtipoVeiculo());
            veiculoparasalvar.setPlacaVeiculo(veiculo.getPlaca());
            Repo.save(veiculoparasalvar);
            return ;
            }
            Agregado agregado=servicoagregado.pegarPorCnpj(veiculo.getCnpjagregado());
            veiculoparasalvar.setAgregadoId(agregado.getIdAgregado());
            veiculoparasalvar.setTipoVeiculo(veiculo.getSubtipoVeiculo().getTipo());
            veiculoparasalvar.setSubtipoVeiculo(veiculo.getSubtipoVeiculo());
            veiculoparasalvar.setPlacaVeiculo(veiculo.getPlaca());
            Repo.save(veiculoparasalvar);

        }catch(Exception e){
            System.out.println("Não foi possivel realizar o salvamento desse Veiculo");
        System.out.println("Informações desse Veiculo:");
        System.out.println("Placa:"+veiculo.getPlaca());
        System.out.println("Tipo:"+veiculo.getTipoVeiculo());
        System.out.println("Subtipo:"+veiculo.getSubtipoVeiculo());
        System.out.println("Status:"+veiculo.getStatus());
        }
    }

    public Veiculo acharPorPlaca(String placa){
        return Repo.findByplacaVeiculo(placa);
    }
}

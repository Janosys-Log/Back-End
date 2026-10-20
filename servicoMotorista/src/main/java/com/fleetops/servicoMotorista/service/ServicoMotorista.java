package com.fleetops.servicoMotorista.service;

import com.fleetops.servicoMotorista.domain.Motorista;
import com.fleetops.servicoMotorista.domain.Veiculo;
import com.fleetops.servicoMotorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.servicoMotorista.repository.MotoristaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoMotorista {
    @Autowired
    private MotoristaRepository motoristaRepo;

    @Autowired
    private ServicoAgregado servicoAgregado;

    @Autowired
    private ServicoVeiculo servicoVeiculo;

    public Motorista pegarPorCPF(String nome) {
        return motoristaRepo.findBycpfMotorista(nome);
    }

    public void SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(VeiculoDTO csvVeiculo) {
        try {
            Motorista motoristaBanco = new Motorista();
            Veiculo veiculo = new Veiculo();
            veiculo.setAgregadoId(
                    servicoAgregado.pegarPorCnpj(csvVeiculo.getCnpjagregado()).getIdAgregado());
            veiculo.setPlacaVeiculo(csvVeiculo.getPlaca());
            veiculo.setSubtipoVeiculo(csvVeiculo.getSubtipoVeiculo());
            veiculo.setTipoVeiculo(csvVeiculo.getSubtipoVeiculo().getTipo());
            if (servicoVeiculo.existe(csvVeiculo.getPlaca())) {
                veiculo = servicoVeiculo.acharPorPlaca(csvVeiculo.getPlaca());
            }

            if (motoristaRepo.existsByCpfMotorista(csvVeiculo.getCpfmotorista())) {
                motoristaBanco = motoristaRepo.findBycpfMotorista(csvVeiculo.getCpfmotorista());
                motoristaBanco.setContatoMotorista(csvVeiculo.getContatomotorista());
                motoristaBanco.setVeiculo(veiculo);
                motoristaRepo.save(motoristaBanco);
                return;
            }
            motoristaBanco.setVeiculo(veiculo);
            motoristaBanco.setContatoMotorista(csvVeiculo.getContatomotorista());
            motoristaBanco.setCpfMotorista(csvVeiculo.getCpfmotorista());
            motoristaBanco.setNomeMotorista(csvVeiculo.getMotorista());
            motoristaBanco.setAgregadoId(
                    servicoAgregado.pegarPorCnpj(csvVeiculo.getCnpjagregado()).getIdAgregado());
            motoristaRepo.save(motoristaBanco);
        } catch (Exception e) {
            System.out.println("Não foi possivel realizar o salvamento desse Motorista");
            System.out.println("Informações desse Motorista:");
            System.out.println("Nome:" + csvVeiculo.getMotorista());
            System.out.println("Cpf:" + csvVeiculo.getCpfmotorista());
            System.out.println("Contato:" + csvVeiculo.getContatomotorista());
        }
    }
}

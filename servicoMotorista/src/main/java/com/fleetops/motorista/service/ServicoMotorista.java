package com.fleetops.motorista.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
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

    public List<Motorista> listar(StatusMotorista status, List<Long> veiculoIds) {
        boolean temVeiculos = veiculoIds != null && !veiculoIds.isEmpty();

        if (status != null && temVeiculos) {
            return motoristaRepo.findByStatusAndVeiculoIdVeiculoIn(status, veiculoIds);
        }
        if (status != null) {
            return motoristaRepo.findByStatus(status);
        }
        if (temVeiculos) {
            return motoristaRepo.findByVeiculoIdVeiculoIn(veiculoIds);
        }
        return motoristaRepo.findAll();
    }

    public List<Motorista> listarDisponiveis() {
        return motoristaRepo.findByStatus(StatusMotorista.Disponivel);
    }

    public Optional<Motorista> buscarPorId(Long id) {
        return motoristaRepo.findById(id);
    }

    public Motorista pegarPorCPF(String cpf) {
        return motoristaRepo.findBycpfMotorista(cpf);
    }

    public void SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(VeiculoDTO csvVeiculo) {
        try {
            Motorista motoristaBanco = new Motorista();
            Veiculo veiculo = new Veiculo();
            veiculo.setAgregadoId(servicoAgregado.pegarPorCnpj(csvVeiculo.getCnpjagregado()).getIdAgregado());
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
            motoristaBanco.setAgregadoId(servicoAgregado.pegarPorCnpj(csvVeiculo.getCnpjagregado()).getIdAgregado());
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
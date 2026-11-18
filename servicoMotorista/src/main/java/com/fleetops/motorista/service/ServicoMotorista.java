package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.domain.Motorista;
import com.fleetops.motorista.domain.StatusMotorista;
import com.fleetops.motorista.domain.Veiculo;
import com.fleetops.motorista.dto.MotoristaDTO;
import com.fleetops.motorista.dto.MotoristaRendimentoDTO;
import com.fleetops.motorista.dto.RentabilidadeDTO;
import com.fleetops.motorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.motorista.repository.MotoristaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        return motoristaRepo.findBycpfMotorista(cpf);
    }

    public Motorista pegarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        return motoristaRepo.findFirstBynomeMotorista(nome).orElse(null);
    }

    public Motorista salvar(Motorista motorista) {
        return motoristaRepo.save(motorista);
    }

    public Motorista criarComDadosManifesto(String nome, String cpf, Veiculo veiculo, Agregado agregado) {
        if (nome == null || nome.isBlank()) {
            nome = "MOTORISTA NÃO INFORMADO";
        }
        if (cpf == null || cpf.isBlank()) {
            cpf = gerarCpfTemporario(nome);
        }
        if (motoristaRepo.existsByCpfMotorista(cpf)) {
            Motorista m = motoristaRepo.findBycpfMotorista(cpf);
            if (m != null) {
                if (m.getVeiculo() == null && veiculo != null) {
                    m.setVeiculo(veiculo);
                    try {
                        motoristaRepo.save(m);
                    } catch (Exception ignored) {
                    }
                }
                return m;
            }
        }
        Motorista motorista = new Motorista();
        motorista.setNomeMotorista(nome);
        motorista.setCpfMotorista(cpf);
        motorista.setContatoMotorista("NÃO INFORMADO");
        motorista.setVeiculo(veiculo);
        if (agregado != null) {
            motorista.setAgregadoId(agregado.getIdAgregado());
        }
        try {
            return motoristaRepo.save(motorista);
        } catch (Exception e) {
            Motorista m = motoristaRepo.findBycpfMotorista(cpf);
            if (m != null) {
                return m;
            }
            return motorista;
        }
    }

    public void SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(VeiculoDTO csvVeiculo) {
        try {
            Veiculo veiculo = servicoVeiculo.acharPorPlaca(csvVeiculo.getPlaca());
            String cpf = csvVeiculo.getCpfmotorista();
            String nome = csvVeiculo.getMotorista();

            if (nome == null || nome.isBlank() || nome.equalsIgnoreCase("NÃO INFORMADO")) {
                return;
            }

            Motorista motoristaBanco = null;
            if (cpf != null && !cpf.isBlank() && motoristaRepo.existsByCpfMotorista(cpf)) {
                motoristaBanco = motoristaRepo.findBycpfMotorista(cpf);
            } else if (motoristaRepo.existsBynomeMotorista(nome)) {
                motoristaBanco = motoristaRepo.findFirstBynomeMotorista(nome).orElse(null);
            }

            if (motoristaBanco != null) {
                if (veiculo != null) {
                    motoristaBanco.setVeiculo(veiculo);
                }
                if (csvVeiculo.getContatomotorista() != null
                        && !csvVeiculo.getContatomotorista().isBlank()) {
                    motoristaBanco.setContatoMotorista(csvVeiculo.getContatomotorista());
                }
                motoristaRepo.save(motoristaBanco);
                return;
            }

            motoristaBanco = new Motorista();
            motoristaBanco.setNomeMotorista(nome);
            motoristaBanco.setCpfMotorista(cpf != null && !cpf.isBlank() ? cpf : gerarCpfTemporario(nome));
            motoristaBanco.setContatoMotorista(
                    csvVeiculo.getContatomotorista() != null
                                    && !csvVeiculo.getContatomotorista().isBlank()
                            ? csvVeiculo.getContatomotorista()
                            : "NÃO INFORMADO");
            motoristaBanco.setVeiculo(veiculo);
            if (veiculo != null && veiculo.getAgregadoId() != null) {
                motoristaBanco.setAgregadoId(veiculo.getAgregadoId());
            }
            motoristaRepo.save(motoristaBanco);

        } catch (Exception e) {
            System.out.println("Não foi possivel realizar o salvamento desse Motorista: " + e.getMessage());
        }
    }

    private String gerarCpfTemporario(String base) {
        long hash = Math.abs((base != null ? base : "").hashCode()) % 100000000L;
        return String.format("999%08d", hash);
    }

    public List<MotoristaRendimentoDTO> rankingMotorista(LocalDate inicio, LocalDate fim) {
        LocalDate mesAnterior = inicio.withDayOfMonth(1).minusMonths(1);
        LocalDate mesAtual = inicio.withDayOfMonth(1);
        LocalDate limiteMesAtual = fim.plusDays(1);
        List<MotoristaRendimentoDTO> listaMoto = motoristaRepo.rankingMotoristas(inicio, fim).stream()
                .map(item -> new MotoristaRendimentoDTO(
                        MotoristaDTO.fromEntity(item.motorista()),
                        item.rendimento() != null
                                ? new RentabilidadeDTO(
                                        item.rendimento().mes() != null
                                                ? item.rendimento().mes().longValue()
                                                : null,
                                        item.rendimento().ano() != null
                                                ? item.rendimento().ano().longValue()
                                                : null,
                                        item.rendimento().rendimentoMedio(),
                                        motoristaRepo.crescimentoRendaMedia(
                                                item.motorista().getIdMotorista(),
                                                mesAnterior,
                                                mesAtual,
                                                limiteMesAtual),
                                        item.rendimento().numeroManifestos(),
                                        item.rendimento().rendimentoTotal())
                                : null))
                .toList();
        return listaMoto;
    }

    public List<MotoristaRendimentoDTO> rankingMotoristaGrandeSp(LocalDate inicio, LocalDate fim) {
        LocalDate mesAnterior = inicio.withDayOfMonth(1).minusMonths(1);
        LocalDate mesAtual = inicio.withDayOfMonth(1);
        LocalDate limiteMesAtual = fim;
        List<MotoristaRendimentoDTO> listaMoto = motoristaRepo.rankingMotoristasGrandeSp(inicio, fim).stream()
                .map(item -> new MotoristaRendimentoDTO(
                        MotoristaDTO.fromEntity(item.motorista()),
                        item.rendimento() != null
                                ? new RentabilidadeDTO(
                                        item.rendimento().mes() != null
                                                ? item.rendimento().mes().longValue()
                                                : null,
                                        item.rendimento().ano() != null
                                                ? item.rendimento().ano().longValue()
                                                : null,
                                        item.rendimento().rendimentoMedio(),
                                        motoristaRepo.crescimentoRendaMediaGrandeSP(
                                                item.motorista().getIdMotorista(),
                                                mesAnterior,
                                                mesAtual,
                                                limiteMesAtual),
                                        item.rendimento().numeroManifestos(),
                                        item.rendimento().rendimentoTotal())
                                : null))
                .toList();
        return listaMoto;
    }
}

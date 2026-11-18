package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.domain.SubtipoVeiculo;
import com.fleetops.motorista.domain.TipoVeiculo;
import com.fleetops.motorista.domain.Veiculo;
import com.fleetops.motorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.motorista.repository.VeiculoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoVeiculo {

    @Autowired
    private VeiculoRepository Repo;

    @Autowired
    private ServicoAgregado servicoagregado;

    public List<Veiculo> listar(TipoVeiculo tipo, SubtipoVeiculo subtipo) {
        if (tipo != null) {
            return Repo.findByTipoVeiculo(tipo);
        }
        if (subtipo != null) {
            return Repo.findBySubtipoVeiculo(subtipo);
        }
        return Repo.findAll();
    }

    public Optional<Veiculo> buscarPorId(Long id) {
        return Repo.findById(id);
    }

    public List<Veiculo> listarPorTipo(TipoVeiculo tipo) {
        return Repo.findByTipoVeiculo(tipo);
    }

    public boolean existe(String Placa) {
        return Repo.existsByplacaVeiculo(Placa);
    }

    public Veiculo acharPorPlaca(String placa) {
        return Repo.findByplacaVeiculo(placa);
    }

    public Veiculo criarComPlacaEAgregado(String placa, Agregado agregado) {
        if (existe(placa)) {
            return acharPorPlaca(placa);
        }
        Veiculo veiculo = new Veiculo();
        veiculo.setPlacaVeiculo(placa);
        if (agregado != null) {
            veiculo.setAgregadoId(agregado.getIdAgregado());
        }
        return Repo.save(veiculo);
    }

    public Veiculo SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(VeiculoDTO veiculo) {
        try {
            Veiculo veiculoparasalvar = new Veiculo();
            if (existe(veiculo.getPlaca())) {
                veiculoparasalvar = Repo.findByplacaVeiculo(veiculo.getPlaca());
                if (veiculo.getSubtipoVeiculo() != null) {
                    veiculoparasalvar.setSubtipoVeiculo(veiculo.getSubtipoVeiculo());
                    veiculoparasalvar.setTipoVeiculo(veiculo.getSubtipoVeiculo().getTipo());
                }
                return Repo.save(veiculoparasalvar);
            }

            Agregado agregado = null;
            if (veiculo.getCnpjagregado() != null && !veiculo.getCnpjagregado().isBlank()) {
                agregado = servicoagregado.pegarPorCnpj(veiculo.getCnpjagregado());
            }
            if (agregado == null
                    && veiculo.getAgregado() != null
                    && !veiculo.getAgregado().isBlank()) {
                agregado = servicoagregado.pegarPorNome(veiculo.getAgregado());
            }
            if (agregado == null) {
                agregado = servicoagregado.SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(veiculo);
            }

            if (agregado != null) {
                veiculoparasalvar.setAgregadoId(agregado.getIdAgregado());
            }
            if (veiculo.getSubtipoVeiculo() != null) {
                veiculoparasalvar.setTipoVeiculo(veiculo.getSubtipoVeiculo().getTipo());
                veiculoparasalvar.setSubtipoVeiculo(veiculo.getSubtipoVeiculo());
            }
            veiculoparasalvar.setPlacaVeiculo(veiculo.getPlaca());
            return Repo.save(veiculoparasalvar);

        } catch (Exception e) {
            System.out.println("Não foi possivel realizar o salvamento desse Veiculo: " + e.getMessage());
            return null;
        }
    }
}

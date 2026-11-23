package com.fleetops.motorista.service;

import com.fleetops.motorista.domain.Agregado;
import com.fleetops.motorista.ouvinte.dto.response.VeiculoDTO;
import com.fleetops.motorista.repository.AgregadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoAgregado {
    @Autowired
    private AgregadoRepository Repo;

    public Agregado pegarPorCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return null;
        }
        return Repo.findBycnpjAgregado(cnpj);
    }

    public Agregado pegarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        return Repo.findBynomeAgregado(nome);
    }

    public Agregado criarComDadosManifesto(String nome, String cnpj) {
        if (nome == null || nome.isBlank()) {
            nome = "FROTA PRÓPRIA";
        }
        if (cnpj == null || cnpj.isBlank()) {
            cnpj = gerarCnpjTemporario(nome);
        }
        if (Repo.existsBycnpjAgregado(cnpj)) {
            return Repo.findBycnpjAgregado(cnpj);
        }
        Agregado agregado = new Agregado();
        agregado.setNomeAgregado(nome);
        agregado.setCnpjAgregado(cnpj);
        agregado.setContatoAgregado("NÃO INFORMADO");
        return Repo.save(agregado);
    }

    public Agregado SalvaOuAtualizaNoBancoComAsInformacoesDoCsv(VeiculoDTO csvVeiculo) {
        try {
            String nome = csvVeiculo.getAgregado() != null
                            && !csvVeiculo.getAgregado().isBlank()
                    ? csvVeiculo.getAgregado()
                    : "FROTA PRÓPRIA";
            String cnpj = csvVeiculo.getCnpjagregado();
            if (cnpj == null || cnpj.isBlank()) {
                if (Repo.existsBynomeAgregado(nome)) {
                    return Repo.findBynomeAgregado(nome);
                }
                cnpj = gerarCnpjTemporario(nome);
            }

            if (Repo.existsBycnpjAgregado(cnpj)) {
                Agregado agregado = Repo.findBycnpjAgregado(cnpj);
                if (csvVeiculo.getContatoagregado() != null
                        && !csvVeiculo.getContatoagregado().isBlank()) {
                    agregado.setContatoAgregado(csvVeiculo.getContatoagregado());
                }
                return Repo.save(agregado);
            }

            Agregado agregado = new Agregado();
            agregado.setNomeAgregado(nome);
            agregado.setCnpjAgregado(cnpj);
            agregado.setContatoAgregado(
                    csvVeiculo.getContatoagregado() != null
                                    && !csvVeiculo.getContatoagregado().isBlank()
                            ? csvVeiculo.getContatoagregado()
                            : "NÃO INFORMADO");
            return Repo.save(agregado);

        } catch (Exception e) {
            System.out.println("Não foi possivel realizar o salvamento desse Agregado: " + e.getMessage());
            return null;
        }
    }

    private String gerarCnpjTemporario(String base) {
        long hash = Math.abs((base != null ? base : "").hashCode()) % 100000000000L;
        return String.format("TMP%011d", hash);
    }
}

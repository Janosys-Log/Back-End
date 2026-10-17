package com.fleetops.motorista.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fleetops.motorista.domain.SubtipoVeiculo;
import com.fleetops.motorista.domain.TipoVeiculo;
import com.fleetops.motorista.domain.Veiculo;
import com.fleetops.motorista.domain.CategoriaVeiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByTipoVeiculo(TipoVeiculo tipoVeiculo);

    List<Veiculo> findBySubtipoVeiculo(SubtipoVeiculo subtipoVeiculo);

    boolean existsByplacaVeiculo(String placa);

    Veiculo findByplacaVeiculo(String placa);
}

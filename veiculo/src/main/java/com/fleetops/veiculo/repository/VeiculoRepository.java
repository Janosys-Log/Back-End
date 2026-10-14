package com.fleetops.veiculo.repository;

import com.fleetops.veiculo.domain.SubtipoVeiculo;
import com.fleetops.veiculo.domain.TipoVeiculo;
import com.fleetops.veiculo.domain.Veiculo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByTipoVeiculo(TipoVeiculo tipoVeiculo);

    List<Veiculo> findBySubtipoVeiculo(SubtipoVeiculo subtipoVeiculo);
}

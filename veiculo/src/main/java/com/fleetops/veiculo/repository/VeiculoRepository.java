package com.fleetops.veiculo.repository;

import com.fleetops.veiculo.domain.TipoVeiculos;
import com.fleetops.veiculo.domain.Veiculo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByTipoVeiculo(TipoVeiculos tipoVeiculo);
}

package com.fleetops.servicoMotorista.repository;

import com.fleetops.servicoMotorista.domain.SubtipoVeiculo;
import com.fleetops.servicoMotorista.domain.TipoVeiculo;
import com.fleetops.servicoMotorista.domain.Veiculo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByTipoVeiculo(TipoVeiculo tipoVeiculo);

    List<Veiculo> findBySubtipoVeiculo(SubtipoVeiculo subtipoVeiculo);

    boolean existsByplacaVeiculo(String placa);

    Veiculo findByplacaVeiculo(String placa);
}



CREATE TABLE IF NOT EXISTS `veiculo` (
  `id_veiculo` INT PRIMARY KEY AUTO_INCREMENT,
  `placa_veiculo` VARCHAR(7) UNIQUE NOT NULL,
  `ano_fabricacao` INT,
  `tipo_veiculo` VARCHAR(50),
  `subtipo_veiculo` VARCHAR(50),
  `fk_id_agregado` INT NOT NULL,
  CONSTRAINT `fk_veiculo_agregado` FOREIGN KEY (`fk_id_agregado`) REFERENCES `agregado` (`id_agregado`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

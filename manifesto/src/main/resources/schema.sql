
CREATE TABLE IF NOT EXISTS `manifesto` (
  `id_manifesto` INT PRIMARY KEY AUTO_INCREMENT,
  `data_manifesto` DATE,
  `destino` VARCHAR(100),
  `valor_recebido` DECIMAL(12,2),
  `frete` DECIMAL(12,2),
  `aereo` BOOLEAN DEFAULT FALSE,
  `fk_id_motorista` INT NOT NULL,
  `fk_id_agregado` INT NOT NULL,
  `fk_id_veiculo` INT NOT NULL,
  CONSTRAINT `fk_manifesto_motorista` FOREIGN KEY (`fk_id_motorista`) REFERENCES `motorista` (`id_motorista`) ON UPDATE CASCADE,
  CONSTRAINT `fk_manifesto_agregado` FOREIGN KEY (`fk_id_agregado`) REFERENCES `agregado` (`id_agregado`) ON UPDATE CASCADE,
  CONSTRAINT `fk_manifesto_veiculo` FOREIGN KEY (`fk_id_veiculo`) REFERENCES `veiculo` (`id_veiculo`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

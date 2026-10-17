
CREATE TABLE IF NOT EXISTS `avaliacao` (
  `id_comentario` INT PRIMARY KEY AUTO_INCREMENT,
  `fk_id_usuario` INT NOT NULL,
  `fk_id_motorista` INT NOT NULL,
  `fk_id_manifesto` INT NOT NULL,
  `feedback` VARCHAR(500),
  `nota_comentario` INT,
  CONSTRAINT `fk_avaliacao_usuario` FOREIGN KEY (`fk_id_usuario`) REFERENCES `usuario` (`id_usuario`) ON UPDATE CASCADE,
  CONSTRAINT `fk_avaliacao_motorista` FOREIGN KEY (`fk_id_motorista`) REFERENCES `motorista` (`id_motorista`) ON UPDATE CASCADE,
  CONSTRAINT `fk_avaliacao_manifesto` FOREIGN KEY (`fk_id_manifesto`) REFERENCES `manifesto` (`id_manifesto`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


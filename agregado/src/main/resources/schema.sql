
CREATE TABLE IF NOT EXISTS `agregado` (
  `id_agregado` INT PRIMARY KEY AUTO_INCREMENT,
  `contato_agregado` VARCHAR(20),
  `cnpj_agregado` VARCHAR(14) UNIQUE NOT NULL,
  `nome_agregado` VARCHAR(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

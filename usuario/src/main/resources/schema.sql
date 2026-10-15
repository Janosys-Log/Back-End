
CREATE TABLE IF NOT EXISTS `usuario` (
  `id_usuario` INT PRIMARY KEY AUTO_INCREMENT,
  `permissao_gerente` BOOLEAN DEFAULT FALSE,
  `nome_usuario` VARCHAR(100) NOT NULL,
  `email_usuario` VARCHAR(150) UNIQUE NOT NULL,
  `senha_usuario` VARCHAR(255) NOT NULL,
  `perfil_acesso` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

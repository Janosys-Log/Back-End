package com.fleetops.usuario.seeder;

import com.fleetops.usuario.domain.Papel;
import com.fleetops.usuario.domain.PerfilAcesso;
import com.fleetops.usuario.domain.Usuario;
import com.fleetops.usuario.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPrimarioSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuarioPrimarioSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin.login:admin}")
    private String adminLogin;

    @Value("${app.security.admin.nome:Administrador Primário}")
    private String adminNome;

    @Value("${app.security.admin.email:admin@janosys.com}")
    private String adminEmail;

    @Value("${app.security.admin.password:Admin@123456}")
    private String adminPassword;

    public UsuarioPrimarioSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmailUsuario(adminEmail).isEmpty()) {
            Usuario primario = Usuario.builder()
                    .nomeUsuario(adminNome)
                    .emailUsuario(adminEmail)
                    .loginUsuario(adminLogin)
                    .senhaUsuario(passwordEncoder.encode(adminPassword))
                    .perfilAcesso(PerfilAcesso.GERENCIA)
                    .papel(Papel.GERENTE_OPERACIONAL)
                    .primario(true)
                    .ativo(true)
                    .build();

            usuarioRepository.save(primario);
            log.info(">>> Usuário primário criado com sucesso: {} (Perfil: {}) <<<", adminEmail, PerfilAcesso.GERENCIA);
        } else {
            log.info(">>> Usuário primário já existe no banco: {} (Ignorando seed) <<<", adminEmail);
        }
    }
}

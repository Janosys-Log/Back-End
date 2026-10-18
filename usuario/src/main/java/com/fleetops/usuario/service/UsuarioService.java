package com.fleetops.usuario.service;

import com.fleetops.usuario.domain.Papel;
import com.fleetops.usuario.domain.PerfilAcesso;
import com.fleetops.usuario.domain.Usuario;
import com.fleetops.usuario.dto.UsuarioCreateRequest;
import com.fleetops.usuario.dto.UsuarioResponse;
import com.fleetops.usuario.dto.UsuarioUpdateRequest;
import com.fleetops.usuario.exception.ConflitoException;
import com.fleetops.usuario.exception.NaoEncontradoException;
import com.fleetops.usuario.exception.RegraNegocioException;
import com.fleetops.usuario.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository repositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repositorio, PasswordEncoder passwordEncoder) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repositorio.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.from(obter(id));
    }

    @Transactional
    public UsuarioResponse criar(UsuarioCreateRequest r) {
        validarPapel(r.perfil(), r.papel());
        if (repositorio.existsByEmailUsuario(r.email())) {
            throw new ConflitoException("Já existe um usuário com este e-mail.");
        }
        if (repositorio.existsByLoginUsuario(r.login())) {
            throw new ConflitoException("Já existe um usuário com este nome de usuário.");
        }

        Usuario usuario = Usuario.builder()
                .nomeUsuario(r.nome())
                .emailUsuario(r.email())
                .loginUsuario(r.login())
                .senhaUsuario(passwordEncoder.encode(r.senha()))
                .perfilAcesso(r.perfil())
                .papel(r.papel())
                .primario(false)
                .ativo(true)
                .build();

        return UsuarioResponse.from(repositorio.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest r) {
        Usuario usuario = obter(id);
        validarPapel(r.perfil(), r.papel());

        if (Boolean.TRUE.equals(usuario.getPrimario()) && r.perfil() != PerfilAcesso.GERENCIA) {
            throw new RegraNegocioException("O usuário primário deve manter o perfil Gerência.");
        }
        if (repositorio.existsByEmailUsuarioAndIdUsuarioNot(r.email(), id)) {
            throw new ConflitoException("Já existe um usuário com este e-mail.");
        }
        if (repositorio.existsByLoginUsuarioAndIdUsuarioNot(r.login(), id)) {
            throw new ConflitoException("Já existe um usuário com este nome de usuário.");
        }

        usuario.setNomeUsuario(r.nome());
        usuario.setEmailUsuario(r.email());
        usuario.setLoginUsuario(r.login());
        usuario.setPerfilAcesso(r.perfil());
        usuario.setPapel(r.papel());
        if (r.senha() != null && !r.senha().isBlank()) {
            usuario.setSenhaUsuario(passwordEncoder.encode(r.senha()));
        }
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse desativar(Long id, String emailLogado) {
        Usuario alvo = obter(id);
        if (alvo.getEmailUsuario().equalsIgnoreCase(emailLogado)) {
            throw new RegraNegocioException("Você não pode desativar a sua própria conta.");
        }
        if (Boolean.TRUE.equals(alvo.getPrimario())) {
            throw new RegraNegocioException("O usuário primário não pode ser desativado.");
        }
        alvo.setAtivo(false);
        return UsuarioResponse.from(alvo);
    }

    @Transactional
    public UsuarioResponse ativar(Long id) {
        Usuario usuario = obter(id);
        usuario.setAtivo(true);
        return UsuarioResponse.from(usuario);
    }

    private Usuario obter(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado."));
    }

    private void validarPapel(PerfilAcesso perfil, Papel papel) {
        if (!papel.pertenceA(perfil)) {
            throw new RegraNegocioException("O papel informado não pertence ao perfil selecionado.");
        }
    }
}

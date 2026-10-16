
package br.edu.fiap.banco.service;

import br.edu.fiap.banco.dto.LoginRequest;
import br.edu.fiap.banco.entity.Usuario;
import br.edu.fiap.banco.repository.UsuarioRepository;
import br.edu.fiap.banco.security.SenhaBCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthService {

    private final UsuarioRepository usuarios;
    private final SenhaBCrypt senhas;

    @Inject
    public AuthService(
            UsuarioRepository usuarios,
            SenhaBCrypt senhas
    ) {
        this.usuarios = usuarios;
        this.senhas = senhas;
    }

    public Usuario autenticar(LoginRequest request) {

        Usuario usuario = usuarios.buscaPorEmail(request.email())
                .orElseThrow(this::credenciaisInvalidas);

        if (!usuario.isAtivo()) {
            throw credenciaisInvalidas();
        }

        if (!senhas.confere(request.senha(), usuario.getSenha())) {
            throw credenciaisInvalidas();
        }

        return usuario;
    }

    private WebApplicationException credenciaisInvalidas() {
        return new WebApplicationException(
                "E-mail ou senha inválidos",
                Response.Status.UNAUTHORIZED
        );
    }
}

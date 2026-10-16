package br.edu.fiap.banco.controller;

import br.edu.fiap.banco.dto.LoginRequest;
import br.edu.fiap.banco.dto.LoginResponse;
import br.edu.fiap.banco.entity.Usuario;
import br.edu.fiap.banco.security.JwtService;
import br.edu.fiap.banco.service.AuthService;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;
    private final JwtService jwtService;

    @Inject
    public AuthResource(
            AuthService authService,
            JwtService jwtService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(
            @NotNull @Valid LoginRequest request
    ) {

        Usuario usuario = authService.autenticar(request);

        String token = jwtService.gerarToken(usuario);

        LoginResponse resposta = LoginResponse.bearer(token);

        return Response.ok(resposta).build();
    }
}

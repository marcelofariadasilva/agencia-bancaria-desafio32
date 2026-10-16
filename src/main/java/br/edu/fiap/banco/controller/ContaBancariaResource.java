package br.edu.fiap.banco.controller;

import br.edu.fiap.banco.dto.ContaBancariaRequest;
import br.edu.fiap.banco.dto.ContaBancariaResponse;
import br.edu.fiap.banco.service.ContaBancariaService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("/api/contas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContaBancariaResource {

    private final ContaBancariaService service;

    /** Injecao por construtor para manter a dependencia explicita. */
    @Inject
    public ContaBancariaResource(ContaBancariaService service) {
        this.service = service;
    }

    /** GET /api/contas - Lista todas as contas. */
    @GET
    public List<ContaBancariaResponse> listar() {
        return service.listar();
    }

    /**
     * GET /api/contas/{id}
     * Exige JWT valido com o papel USER.
     */
    @GET
    @Path("/{id}")
    @RolesAllowed("USER")
    public ContaBancariaResponse buscarPorId(
            @PathParam("id") Long id
    ) {
        return service.buscarPorId(id);
    }

    /** GET /api/contas/pessoa/{pessoaId} */
    @GET
    @Path("/pessoa/{pessoaId}")
    public List<ContaBancariaResponse> listarPorPessoa(
            @PathParam("pessoaId") Long id
    ) {
        return service.listarPorTitular(id);
    }

    /** POST /api/contas - Abre uma conta. */
    @POST
    public Response abrir(
            @NotNull @Valid ContaBancariaRequest request,
            @Context UriInfo uriInfo
    ) {
        ContaBancariaResponse resposta = service.criarConta(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(resposta.id().toString())
                .build();

        return Response.created(location)
                .entity(resposta)
                .build();
    }

    /** PUT /api/contas/{id} - Atualiza uma conta. */
    @PUT
    @Path("/{id}")
    public ContaBancariaResponse atualizar(
            @PathParam("id") Long id,
            @NotNull @Valid ContaBancariaRequest request
    ) {
        return service.atualizar(id, request);
    }

    /** DELETE /api/contas/{id} - Encerra uma conta. */
    @DELETE
    @Path("/{id}")
    public Response encerrar(@PathParam("id") Long id) {
        service.encerrar(id);
        return Response.noContent().build();
    }
}

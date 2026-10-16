package br.edu.fiap.banco.service;

import br.edu.fiap.banco.dto.ContaBancariaRequest;
import br.edu.fiap.banco.dto.ContaBancariaResponse;
import br.edu.fiap.banco.entity.ContaBancaria;
import br.edu.fiap.banco.entity.Pessoa;
import br.edu.fiap.banco.entity.TipoConta;
import br.edu.fiap.banco.exception.ContaJaCadastradaException;
import br.edu.fiap.banco.exception.ContaNaoEncontradaException;
import br.edu.fiap.banco.exception.PessoaNaoEncontradaException;
import br.edu.fiap.banco.exception.TipoContaNaoEncontradoException;
import br.edu.fiap.banco.repository.ContaBancariaRepository;
import br.edu.fiap.banco.repository.PessoaRepository;
import br.edu.fiap.banco.repository.TipoContaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ContaBancariaService {

    private final ContaBancariaRepository contas;
    private final PessoaRepository pessoas;
    private final TipoContaRepository tipos;

    @Inject
    public ContaBancariaService(
            ContaBancariaRepository contas,
            PessoaRepository pessoas,
            TipoContaRepository tipos
    ) {
        this.contas = contas;
        this.pessoas = pessoas;
        this.tipos = tipos;
    }

    /** Lista todas as contas bancarias. */
    public List<ContaBancariaResponse> listar() {
        return contas.listar()
                .stream()
                .map(ContaBancariaResponse::de)
                .toList();
    }

    /** Busca uma conta bancaria pelo ID. */
    public ContaBancariaResponse buscarPorId(Long id) {
        ContaBancaria conta = contas.buscarPorId(id)
                .orElseThrow(ContaNaoEncontradaException::new);

        return ContaBancariaResponse.de(conta);
    }

    /** Lista as contas de determinado titular. */
    public List<ContaBancariaResponse> listarPorTitular(Long titularId) {
        if (pessoas.getPessoaById(titularId).isEmpty()) {
            throw new PessoaNaoEncontradaException();
        }

        return contas.listarPorTitular(titularId)
                .stream()
                .map(ContaBancariaResponse::de)
                .toList();
    }

    /** Cria uma nova conta bancaria. */
    public ContaBancariaResponse criarConta(ContaBancariaRequest request) {

        if (contas.existePorAgenciaENumero(
                request.agencia(),
                request.numero()
        )) {
            throw new ContaJaCadastradaException();
        }

        Pessoa titular = pessoas.getPessoaById(request.titularId())
                .orElseThrow(PessoaNaoEncontradaException::new);

        TipoConta tipoConta = tipos.buscarPorId(request.tipoContaId())
                .orElseThrow(TipoContaNaoEncontradoException::new);

        ContaBancaria novaConta = ContaBancaria.nova(
                request.agencia(),
                request.numero(),
                titular,
                tipoConta
        );

        return ContaBancariaResponse.de(
                contas.salvar(novaConta)
        );
    }

    /** Encerra logicamente uma conta bancaria. */
    public void encerrar(Long id) {
        if (!contas.encerrar(id)) {
            throw new ContaNaoEncontradaException();
        }
    }

    /** Atualiza os dados editaveis de uma conta bancaria. */
    public ContaBancariaResponse atualizar(
            Long id,
            ContaBancariaRequest request
    ) {

        ContaBancaria atual = contas.buscarPorId(id)
                .orElseThrow(ContaNaoEncontradaException::new);

        TipoConta tipo = tipos.buscarPorId(request.tipoContaId())
                .orElseThrow(TipoContaNaoEncontradoException::new);

        return ContaBancariaResponse.de(
                contas.atualizar(
                        atual.atualizar(
                                request.agencia(),
                                request.numero(),
                                tipo
                        )
                )
        );
    }
}

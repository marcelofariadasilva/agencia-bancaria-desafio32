package br.edu.fiap.banco.service;

import br.edu.fiap.banco.dto.UsuarioRequest;
import br.edu.fiap.banco.entity.Usuario;
import br.edu.fiap.banco.repository.UsuarioRepository;
import br.edu.fiap.banco.security.SenhaBCrypt;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    @Test
    void deveEnviarHashBCryptAoRepositorioEmVezDaSenhaOriginal() {
        // Arrange: prepara o cenário
        String senhaOriginal = "SenhaTeste@123";

        RepositorioUsuarioFake repositorio =
                new RepositorioUsuarioFake();

        SenhaBCrypt senhas = new SenhaBCrypt(12);

        UsuarioService service =
                new UsuarioService(repositorio, senhas);

        UsuarioRequest request = new UsuarioRequest(
                "Usuario Teste",
                "teste.bcrypt@exemplo.com",
                senhaOriginal
        );

        // Act: executa o cadastro
        service.cadastrarUsuario(request);

        // Assert: verifica o que chegou ao repositório
        Usuario usuarioSalvo = repositorio.usuarioRecebido;

        assertNotNull(
                usuarioSalvo,
                "O repositorio deve receber um usuario"
        );

        String hashRecebido = usuarioSalvo.getSenha();

        assertNotNull(hashRecebido);

        assertNotEquals(
                senhaOriginal,
                hashRecebido,
                "A senha original nao pode chegar ao repositorio"
        );

        assertTrue(
                hashRecebido.startsWith("$2"),
                "O valor deve possuir o prefixo de um hash BCrypt"
        );

        assertTrue(
                senhas.confere(senhaOriginal, hashRecebido),
                "O hash deve corresponder a senha original"
        );

        assertFalse(
                senhas.confere("SenhaIncorreta", hashRecebido),
                "Uma senha incorreta nao deve corresponder ao hash"
        );
    }

    private static class RepositorioUsuarioFake
            implements UsuarioRepository {

        private Usuario usuarioRecebido;

        @Override
        public boolean existePorEmail(String email) {
            return false;
        }

        @Override
        public Usuario salvar(Usuario usuario) {
            this.usuarioRecebido = usuario;

            return new Usuario(
                    1L,
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getSenha(),
                    usuario.isAtivo()
            );
        }

        @Override
        public Optional<Usuario> buscaPorEmail(String email) {
            return Optional.empty();
        }
    }
}

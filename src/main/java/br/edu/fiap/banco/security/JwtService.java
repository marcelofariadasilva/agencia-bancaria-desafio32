package br.edu.fiap.banco.security;

import br.edu.fiap.banco.entity.Usuario;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    private final String emissor;
    private final long validadeSegundos;

    public JwtService(
            @ConfigProperty(name = "mp.jwt.verify.issuer")
            String emissor,

            @ConfigProperty(
                    name = "security.jwt.expiration-seconds",
                    defaultValue = "3600"
            )
            long validadeSegundos
    ) {
        this.emissor = emissor;
        this.validadeSegundos = validadeSegundos;
    }

    public String gerarToken(Usuario usuario) {

        return Jwt.issuer(emissor)
                .subject(usuario.getEmail())
                .upn(usuario.getEmail())
                .groups(Set.of("USER"))
                .claim("usuarioId", usuario.getId())
                .expiresIn(Duration.ofSeconds(validadeSegundos))
                .sign();
    }
}


package br.edu.fiap.banco.dto;

public record LoginResponse(
        String token,
        String tipo
) {

    public static LoginResponse bearer(String token) {
        return new LoginResponse(token, "Bearer");
    }
}

package co.example.model.usuario.gateways;

import co.example.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface JwtGateway {
    String generateToken(Usuario usuario);
    Mono<Usuario> validarYExtraerUsuario(String token);
}

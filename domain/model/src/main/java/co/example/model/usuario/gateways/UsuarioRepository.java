package co.example.model.usuario.gateways;

import co.example.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> getUsuarioByEmail(String email);
    Mono<Usuario> guardarUsuario(Usuario usuario);
}

package co.example.usecase.usuario;

import co.example.model.usuario.gateways.JwtGateway;
import co.example.model.usuario.gateways.PasswordGateway;
import co.example.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AutenticacionUseCase {
    private final UsuarioRepository usuarioRepository;
    private final PasswordGateway passwordGateway;
    private final JwtGateway jwtGateway;

    public Mono<String> autenticarUsuario(String email, String password) {
        return usuarioRepository.getUsuarioByEmail(email)
                .filter(user -> passwordGateway.validarPassword(password, user.getPassword()))
                .map(jwtGateway::generateToken)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Credenciales no validas")));
    }

}

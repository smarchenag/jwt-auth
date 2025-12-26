package co.example.usecase.usuario;

import co.example.model.exception.BusinessException;
import co.example.model.exception.ErrorType;
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
                .switchIfEmpty(Mono.error(() -> 
                    new BusinessException(ErrorType.INVALID_CREDENTIALS)
                ))
                .flatMap(user -> {
                    boolean passwordValida = passwordGateway.validarPassword(password, user.getPassword());
                    if (!passwordValida) {
                        return Mono.error(
                            new BusinessException(ErrorType.INVALID_CREDENTIALS)
                        );
                    }
                    return Mono.just(jwtGateway.generateToken(user));
                });
    }

}

package co.example.api;

import co.example.api.model.LoginRequest;
import co.example.api.model.RegistroRequest;
import co.example.api.model.UsuarioResponse;
import co.example.usecase.usuario.AutenticacionUseCase;
import co.example.usecase.usuario.RegistroUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final AutenticacionUseCase autenticacionUseCase;
    private final RegistroUseCase registroUseCase;
    private final PasswordEncoder passwordEncoder;

    public Mono<ServerResponse> loginUsuario(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequest.class)
                .flatMap(loginRequest -> autenticacionUseCase.autenticarUsuario(
                    loginRequest.username(), 
                    loginRequest.password()
                ))
                .flatMap(token -> ServerResponse.ok().bodyValue(token));
    }

    public Mono<ServerResponse> registroUsuario(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegistroRequest.class)
                .flatMap(registro -> {
                    String passwordEncriptada = passwordEncoder.encode(registro.password());
                    return registroUseCase.registrarUsuario(
                            registro.nombre(),
                            registro.email(),
                            passwordEncriptada
                    );
                })
                .map(usuario -> new UsuarioResponse(usuario.getName(), usuario.getEmail()))
                .flatMap(response -> ServerResponse.created(null).bodyValue(response));
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("");
    }
}

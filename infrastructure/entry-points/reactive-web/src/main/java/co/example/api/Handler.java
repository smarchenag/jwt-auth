package co.example.api;

import co.example.api.model.ErrorResponse;
import co.example.api.model.LoginRequest;
import co.example.api.model.RegistroRequest;
import co.example.api.model.UsuarioResponse;
import co.example.api.validator.LoginValidator;
import co.example.api.validator.RegistroValidator;
import co.example.api.validator.ValidationException;
import co.example.usecase.usuario.AutenticacionUseCase;
import co.example.usecase.usuario.RegistroUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class Handler {
    private final AutenticacionUseCase autenticacionUseCase;
    private final RegistroUseCase registroUseCase;
    private final PasswordEncoder passwordEncoder;
    private final LoginValidator loginValidator;
    private final RegistroValidator registroValidator;

    public Mono<ServerResponse> loginUsuario(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequest.class)
                .flatMap(loginValidator::validate)
                .flatMap(loginRequest -> autenticacionUseCase.autenticarUsuario(
                    loginRequest.username(), 
                    loginRequest.password()
                ))
                .flatMap(token -> ServerResponse.ok().bodyValue(token))
                .onErrorResume(error -> handleLoginError(error, serverRequest));
    }

    public Mono<ServerResponse> registroUsuario(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegistroRequest.class)
                .flatMap(registroValidator::validate)
                .flatMap(registro -> {
                    String passwordEncriptada = passwordEncoder.encode(registro.password());
                    return registroUseCase.registrarUsuario(
                            registro.nombre(),
                            registro.email(),
                            passwordEncriptada
                    );
                })
                .map(usuario -> new UsuarioResponse(usuario.getName(), usuario.getEmail()))
                .flatMap(response -> ServerResponse.created(URI.create("/usuarios")).bodyValue(response))
                .onErrorResume(error -> handleRegistroError(error, serverRequest));
    }

    private Mono<ServerResponse> handleLoginError(Throwable error, ServerRequest request) {
        if (error instanceof ValidationException) {
            return buildErrorResponse(400, error.getMessage(), "VALIDATION_ERROR", request);
        }
        return buildErrorResponse(401, "Credenciales inválidas", "AUTHENTICATION_FAILED", request);
    }

    private Mono<ServerResponse> handleRegistroError(Throwable error, ServerRequest request) {
        if (error instanceof ValidationException) {
            return buildErrorResponse(400, error.getMessage(), "VALIDATION_ERROR", request);
        }
        return buildErrorResponse(409, "El usuario ya existe", "USER_ALREADY_EXISTS", request);
    }

    private Mono<ServerResponse> buildErrorResponse(
            int status, 
            String message, 
            String errorCode, 
            ServerRequest request) {
        ErrorResponse error = new ErrorResponse(
            status,
            message,
            errorCode,
            LocalDateTime.now(),
            request.path()
        );
        return ServerResponse.status(status).bodyValue(error);
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("");
    }
}

package co.example.api.validator;

import co.example.api.model.LoginRequest;
import reactor.core.publisher.Mono;

public class LoginValidator {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 4;

    public Mono<LoginRequest> validate(LoginRequest request) {
        return Mono.fromCallable(() -> {
            validateUsername(request.username());
            validatePassword(request.password());
            return request;
        })
        .onErrorMap(this::mapValidationError);
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("El username no puede estar vacío");
        }
        if (username.length() < MIN_USERNAME_LENGTH) {
            throw new ValidationException(
                "El username debe tener al menos " + MIN_USERNAME_LENGTH + " caracteres"
            );
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            throw new ValidationException(
                "El username no puede exceder " + MAX_USERNAME_LENGTH + " caracteres"
            );
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ValidationException("La contraseña no puede estar vacía");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException(
                "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres"
            );
        }
    }

    private Throwable mapValidationError(Throwable throwable) {
        if (throwable instanceof ValidationException) {
            return throwable;
        }
        return new ValidationException("Error validando login: " + throwable.getMessage());
    }
}

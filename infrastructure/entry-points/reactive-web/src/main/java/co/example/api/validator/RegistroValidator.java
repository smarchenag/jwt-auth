package co.example.api.validator;

import co.example.api.model.RegistroRequest;
import reactor.core.publisher.Mono;
import java.util.regex.Pattern;

public class RegistroValidator {

    private static final int MIN_NOMBRE_LENGTH = 2;
    private static final int MAX_NOMBRE_LENGTH = 100;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public Mono<RegistroRequest> validate(RegistroRequest request) {
        return Mono.fromCallable(() -> {
            validateNombre(request.nombre());
            validateEmail(request.email());
            validatePassword(request.password());
            return request;
        })
        .onErrorMap(this::mapValidationError);
    }

    private void validateNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidationException("El nombre no puede estar vacío");
        }
        if (nombre.length() < MIN_NOMBRE_LENGTH) {
            throw new ValidationException(
                "El nombre debe tener al menos " + MIN_NOMBRE_LENGTH + " caracteres"
            );
        }
        if (nombre.length() > MAX_NOMBRE_LENGTH) {
            throw new ValidationException(
                "El nombre no puede exceder " + MAX_NOMBRE_LENGTH + " caracteres"
            );
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("El email no puede estar vacío");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("El email tiene un formato inválido");
        }
        if (email.length() > 255) {
            throw new ValidationException("El email es demasiado largo");
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
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new ValidationException(
                "La contraseña no puede exceder " + MAX_PASSWORD_LENGTH + " caracteres"
            );
        }
    }

    private Throwable mapValidationError(Throwable throwable) {
        if (throwable instanceof ValidationException) {
            return throwable;
        }
        return new ValidationException("Error validando registro: " + throwable.getMessage());
    }
}

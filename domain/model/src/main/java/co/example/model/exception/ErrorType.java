package co.example.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorType {
    INVALID_CREDENTIALS(401, "Credenciales inválidas"),
    USER_NOT_FOUND(404, "Usuario no encontrado"),
    USER_ALREADY_EXISTS(409, "El usuario ya existe"),
    INVALID_INPUT(400, "Datos de entrada inválidos"),
    UNAUTHORIZED(401, "No autorizado"),
    FORBIDDEN(403, "Acceso prohibido"),
    INTERNAL_ERROR(500, "Error interno del servidor");

    private final int statusCode;
    private final String message;
}

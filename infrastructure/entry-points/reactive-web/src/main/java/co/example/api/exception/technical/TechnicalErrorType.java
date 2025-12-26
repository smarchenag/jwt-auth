package co.example.api.exception.technical;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TechnicalErrorType {
    DATABASE_ERROR(500, "Error en la base de datos"),
    CONNECTION_ERROR(503, "Error de conexión"),
    AUTHENTICATION_ERROR(401, "Error en la autenticación"),
    JWT_GENERATION_ERROR(500, "Error al generar el token JWT"),
    JWT_VALIDATION_ERROR(401, "Error al validar el token JWT"),
    ENCRYPTION_ERROR(500, "Error en la encriptación"),
    EXTERNAL_SERVICE_ERROR(503, "Error en servicio externo"),
    INTERNAL_TECHNICAL_ERROR(500, "Error técnico interno");

    private final int statusCode;
    private final String message;
}

package co.example.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String errorType;
    private LocalDateTime timestamp;

    public ErrorResponse(int statusCode, String message, String errorType) {
        this.statusCode = statusCode;
        this.message = message;
        this.errorType = errorType;
        this.timestamp = LocalDateTime.now();
    }
}

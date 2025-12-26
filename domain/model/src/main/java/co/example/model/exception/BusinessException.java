package co.example.model.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorType errorType;

    public BusinessException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public BusinessException(ErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }

    public int getStatusCode() {
        return errorType.getStatusCode();
    }
}

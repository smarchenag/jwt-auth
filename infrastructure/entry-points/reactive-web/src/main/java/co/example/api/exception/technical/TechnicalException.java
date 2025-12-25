package co.example.api.exception.technical;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {
    private final TechnicalErrorType errorType;

    public TechnicalException(TechnicalErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public TechnicalException(TechnicalErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }

    public TechnicalException(TechnicalErrorType errorType, Throwable cause) {
        super(errorType.getMessage(), cause);
        this.errorType = errorType;
    }

    public TechnicalException(TechnicalErrorType errorType, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorType = errorType;
    }

    public int getStatusCode() {
        return errorType.getStatusCode();
    }
}

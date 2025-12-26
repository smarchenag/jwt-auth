package co.example.api.exception;

import co.example.api.exception.technical.TechnicalException;
import co.example.model.exception.BusinessException;
import co.example.model.exception.ErrorResponse;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                                  ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageReaders(configurer.getReaders());
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);

        if (error instanceof BusinessException) {
            return handleBusinessException((BusinessException) error);
        } else if (error instanceof TechnicalException) {
            return handleTechnicalException((TechnicalException) error);
        } else {
            return handleGenericException(error);
        }
    }

    private Mono<ServerResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode(),
                ex.getMessage(),
                ex.getErrorType().name()
        );

        return ServerResponse
                .status(HttpStatus.valueOf(ex.getStatusCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

    private Mono<ServerResponse> handleTechnicalException(TechnicalException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode(),
                ex.getMessage(),
                ex.getErrorType().name()
        );

        return ServerResponse
                .status(HttpStatus.valueOf(ex.getStatusCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

    private Mono<ServerResponse> handleGenericException(Throwable ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "Error interno del servidor",
                "INTERNAL_ERROR"
        );

        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}

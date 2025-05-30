package org.dmdev.bookstore.exception.exceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.dmdev.bookstore.exception.ApiException;
import org.dmdev.bookstore.exception.AuthException;
import org.dmdev.bookstore.exception.UnauthorizedException;
import org.dmdev.bookstore.model.ResponseModel;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(AuthException.class)
    public Mono<ResponseModel> handleAuthException(Throwable ex) {
        return Mono.just(ResponseModel.builder()
                .status(ResponseModel.FAIL_STATUS)
                .message("Internal Server Error: " + ex.getMessage())
                .build());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseModel> handleUnauthorizedException(Throwable ex) {
        return Mono.just(ResponseModel.builder()
                .status(ResponseModel.FAIL_STATUS)
                .message("Internal Server Error: " + ex.getMessage())
                .build());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Mono<ResponseModel> handleExpiredJwtException(Throwable ex) {
        return Mono.just(ResponseModel.builder()
                .status(ResponseModel.FAIL_STATUS)
                .message("Internal Server Error: " + ex.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseModel> handleGenericException(Exception ex) {
        return Mono.just(ResponseModel.builder()
                .status(ResponseModel.FAIL_STATUS)
                .message("Unexpected error: " + ex.getMessage())
                .build());
    }
}

package org.dmdev.bookstore.exception.exceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import org.dmdev.bookstore.exception.AuthException;
import org.dmdev.bookstore.exception.UnauthorizedException;
import org.dmdev.bookstore.model.ResponseModel;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;


@RestControllerAdvice
public class GlobalExceptionHandler {

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

package org.dmdev.bookstore.exception;

public class AuthException extends ApiException{
    public AuthException(String errorCode, String message) {
        super(errorCode, message);
    }
}

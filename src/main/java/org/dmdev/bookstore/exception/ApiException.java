package org.dmdev.bookstore.exception;

public class ApiException  extends RuntimeException{

    protected String errorCode;

    public ApiException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

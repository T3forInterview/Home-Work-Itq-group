package ru.hw.PetrushinNickolay.exception;

import org.springframework.http.HttpStatus;

public class InvalidOperationException extends RuntimeException {
    private final HttpStatus status;

    public InvalidOperationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

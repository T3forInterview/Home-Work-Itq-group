package ru.hw.PetrushinNickolay.dto;

import org.springframework.http.HttpStatus;

public class ErrorResponseDTO {
    private String code;
    private String message;
    private HttpStatus status;

    public ErrorResponseDTO() {
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}

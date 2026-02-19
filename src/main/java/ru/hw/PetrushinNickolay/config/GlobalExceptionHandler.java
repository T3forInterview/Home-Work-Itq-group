package ru.hw.PetrushinNickolay.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.hw.PetrushinNickolay.dto.ErrorResponseDTO;
import ru.hw.PetrushinNickolay.exception.InvalidOperationException;
import ru.hw.PetrushinNickolay.exception.ResourceNotFoundException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger loggerRNFE = LoggerFactory.getLogger(ResourceNotFoundException.class);
    private static final Logger loggerIOE = LoggerFactory.getLogger(InvalidOperationException.class);
    private static final Logger loggerMANE = LoggerFactory.getLogger(MethodArgumentNotValidException.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(ResourceNotFoundException exception) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setCode("Не найдено");
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setStatus(HttpStatus.NOT_FOUND);
        loggerRNFE.warn("Не найдено " + exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationException(InvalidOperationException exception) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setCode("Недопустимая операция");
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setStatus(exception.getStatus());
        loggerIOE.warn("Недопустимая операция " + exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        String errorValidation = ex.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setCode("Ошибки валидации");
        errorResponse.setMessage("Ошибки в полях: " + errorValidation);
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
        loggerMANE.error("Ошибки в полях: " + errorValidation);
        return handleExceptionInternal(ex, errorResponse, headers, HttpStatus.BAD_REQUEST, request);
    }
}

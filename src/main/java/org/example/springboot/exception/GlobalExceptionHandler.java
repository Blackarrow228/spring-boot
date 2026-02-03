package org.example.springboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_MESSAGE = "Значение %s в %s %s";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundError(NotFoundException notFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handleBadRequestError(DuplicateException badRequestException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequestException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleBadRequestValidError(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getAllErrors().stream().filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .map(error -> ERROR_MESSAGE.formatted(error.getRejectedValue(), error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(";\n"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}

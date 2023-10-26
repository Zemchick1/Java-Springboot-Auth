package com.zemka.movieservice.exception;

import com.zemka.movieservice.model.record.ApiError;
import com.zemka.movieservice.model.record.ValidationError;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler { // TODO LOGGER
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleArgumentNotValid(@NotNull MethodArgumentNotValidException ex) {
        List<String> validationErrors = createValidationErrorsList(ex);
        ValidationError validationError = new ValidationError(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                validationErrors);
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(@NotNull BadRequestException ex){
        ApiError apiError = createApiError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(@NotNull ResourceNotFoundException ex){
        ApiError apiError = createApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(){
        ApiError apiError = createApiError("Bad Credentials", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    private ApiError createApiError(String message, int status_code){
        return new ApiError(message, status_code);
    }

    private List<String> createValidationErrorsList(MethodArgumentNotValidException ex){
        List<String> validationErrors = new ArrayList<>();
        ex.getFieldErrors().forEach(fieldError -> validationErrors.add(fieldError.getDefaultMessage()));
        return validationErrors;
    }
}

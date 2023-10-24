package com.zemka.graphicscardservice.model.record;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationError extends ApiError{
    private final List<String> validationErrors;
    @Builder
    public ValidationError(String message, int status_code, List<String> validationErrors) {
        super(message, status_code);
        this.validationErrors = validationErrors;
    }
}

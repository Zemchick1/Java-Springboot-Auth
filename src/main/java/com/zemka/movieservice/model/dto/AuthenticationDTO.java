package com.zemka.movieservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationDTO {
    @Pattern(regexp = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$",
            message = "Invalid Email")
    @Schema(name = "email", example = "zemchickpro@gmail.com")
    private String email;
    @Schema(name = "password", example = "Qwerty12345678")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password should contain minimum eight characters, at least one letter and one number")
    private String password;
}

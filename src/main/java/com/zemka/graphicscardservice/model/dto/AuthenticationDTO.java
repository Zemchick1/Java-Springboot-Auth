package com.zemka.graphicscardservice.model.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationDTO {
    @Pattern(regexp = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$",
            message = "Invalid Email")
    private String email;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password should contain minimum eight characters, at least one letter and one number")
    private String password;
}

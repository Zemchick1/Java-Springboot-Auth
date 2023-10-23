package com.zemka.graphicscardservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationDTO {
    private String email;
    String password;
}

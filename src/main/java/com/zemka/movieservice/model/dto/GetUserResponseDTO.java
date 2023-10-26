package com.zemka.movieservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserResponseDTO { // TODO PROVIDER
    private String email;
    private String role;
}

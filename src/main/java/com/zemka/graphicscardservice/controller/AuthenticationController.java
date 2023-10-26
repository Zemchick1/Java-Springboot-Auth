package com.zemka.graphicscardservice.controller;

import com.zemka.graphicscardservice.model.dto.AuthenticationDTO;
import com.zemka.graphicscardservice.model.dto.GetUserResponseDTO;
import com.zemka.graphicscardservice.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody @Valid @NotNull AuthenticationDTO authenticationDTO,
                                            HttpServletResponse response) {
        return authenticationService.register(authenticationDTO, response);
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody @NotNull AuthenticationDTO authenticationDTO,
                                         HttpServletResponse response) {
        return authenticationService.login(authenticationDTO, response);
    }

    @PostMapping("/logout")
    public void logout() {
        // No need for logout logic here as it's handled by the CustomLogoutFilter
    }

    @PostMapping("/getUser")
    public ResponseEntity<GetUserResponseDTO> getUser(HttpServletRequest request) {
        return authenticationService.getUser(request);
    }
}

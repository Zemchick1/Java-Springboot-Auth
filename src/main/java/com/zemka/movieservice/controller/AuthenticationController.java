package com.zemka.movieservice.controller;

import com.zemka.movieservice.model.dto.AuthenticationDTO;
import com.zemka.movieservice.model.dto.GetUserResponseDTO;
import com.zemka.movieservice.model.record.ApiError;
import com.zemka.movieservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication API", description = "API for Username Password Authentication using JWT (JSON Web Token) and storing the token in cookies.")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new User using a username and password",
    description = """
            This operation allows users to create a new account by providing a unique email and a secure password. Upon successful registration, the system will create a new user account and set Authentication Cookies.

                    - Use the `email` and `password` parameters to specify the desired email and password for the new account.
                    - The system will check for email uniqueness and password security.
                    - If the registration is successful, you will receive a boolean value of 'true', indicating that the new user account has been created, and Authentication Cookies have been set.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added a new User"),
            @ApiResponse(responseCode = "400", description = "User with this email is already exists",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))})
            })
    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody @Valid @NotNull AuthenticationDTO authenticationDTO,
                                            HttpServletResponse response) {
        return authenticationService.register(authenticationDTO, response);
    }

    @Operation(summary = "Login using a username and password",
    description = """
            This operation allows users to authenticate by providing a valid email and password combination. Upon successful authentication, the system will return a boolean value of 'true' and set Authentication Cookies.

                    - Use the `email` and `password` parameters to provide your credentials.
                    - The API will verify the credentials and set Authentication Cookies if authentication is successful.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Bad Credentials",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody @NotNull AuthenticationDTO authenticationDTO,
                                         HttpServletResponse response) {
        return authenticationService.login(authenticationDTO, response);
    }

    @Operation(
            summary = "User Logout",
            description = "This endpoint allows authenticated users to log out. " +
                    "Logging out invalidates Authentication Cookies, making it necessary to re-authenticate for protected resources."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            // TODO
    })
    @PostMapping("/logout")
    public void logout() {
        // No need for logout logic here as it's handled by the CustomLogoutFilter
    }

    @Operation(
            summary = "Get User Information",
            description = "This endpoint allows you to retrieve user information based on Authentication. When Authentication Cookies are set, the system will respond with the user's associated data, such as their email and role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved User Information"),
            @ApiResponse(responseCode = "400", description = "Authentication Cookies not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @PostMapping("/get_user")
    public ResponseEntity<GetUserResponseDTO> getUser(HttpServletRequest request) {
        return authenticationService.getUser(request);
    }
}

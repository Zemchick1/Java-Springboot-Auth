package com.zemka.graphicscardservice.service;

import com.zemka.graphicscardservice.model.dto.AuthenticationDTO;
import com.zemka.graphicscardservice.model.entity.User;
import com.zemka.graphicscardservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import utils.CookieUtils;
import utils.enums.Role;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Value("${jwt_token_cookie_name}")
    private String jwtTokenCookieName;
    @Value("${jwt_token_lifetime}")
    private Integer jwtTokenLifeTime;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<Boolean> register(AuthenticationDTO authenticationDTO,
                                            HttpServletResponse response) {
        if (userRepository.countByEmail(authenticationDTO.getEmail()) != 0) {
            throw new RuntimeException("User with this email is already exists"); // TODO EXCEPTION
        }
        User user = createNewUser(authenticationDTO);
        user = userRepository.save(user);
        String jwt_token = generateAndSaveJwtToken(user);
        setAuthCookie(jwt_token, response);
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<Boolean> login(AuthenticationDTO authenticationDTO,
                                         HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationDTO.getEmail(),
                        authenticationDTO.getPassword()
                )
        );
        User user = userRepository.findByEmail(authenticationDTO.getEmail()).orElseThrow(); // TODO EXCEPTION
        String jwt_token = generateAndSaveJwtToken(user);
        setAuthCookie(jwt_token, response);
        return ResponseEntity.ok(true);
    }

    private User createNewUser(AuthenticationDTO authenticationDTO) {
        return User.builder()
                .email(authenticationDTO.getEmail())
                .password(passwordEncoder.encode(authenticationDTO.getPassword()))
                .role(Role.User)
                .build();
    }

    private void setAuthCookie(String jwt_token, HttpServletResponse response) {
        CookieUtils.addCookie(response, jwtTokenCookieName, jwt_token, jwtTokenLifeTime);
    }

    private String generateAndSaveJwtToken(User user) {
        String jwt_token = jwtService.generateToken(user);
        jwtService.saveToken(jwt_token, user);
        return jwt_token;
    }
}
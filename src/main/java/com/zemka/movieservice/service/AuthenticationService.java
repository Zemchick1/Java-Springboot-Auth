package com.zemka.movieservice.service;

import com.zemka.movieservice.exception.BadRequestException;
import com.zemka.movieservice.model.dto.AuthenticationDTO;
import com.zemka.movieservice.model.dto.GetUserResponseDTO;
import com.zemka.movieservice.model.entity.JwtToken;
import com.zemka.movieservice.model.entity.User;
import com.zemka.movieservice.repository.JwtTokenRepository;
import com.zemka.movieservice.repository.UserRepository;
import com.zemka.movieservice.utils.CookieUtils;
import com.zemka.movieservice.utils.enums.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final JwtTokenRepository jwtTokenRepository;

    public ResponseEntity<Boolean> register(AuthenticationDTO authenticationDTO,
                                            HttpServletResponse response) {
        if (userRepository.countByEmail(authenticationDTO.getEmail()) != 0) {
            throw new BadRequestException("User with this email is already exists");
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
        User user = userRepository.findByEmail(authenticationDTO.getEmail()).orElseThrow(
                () -> new BadRequestException("Bad Request"));
        String jwt_token = generateAndSaveJwtToken(user);
        setAuthCookie(jwt_token, response);
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        String jwtToken_String = getJwtTokenFromRequest(request);
        setJwtTokenRevokedAndSave(jwtToken_String);
        clearAuthentication(response);
        return ResponseEntity.ok(true);
    }

    private void setJwtTokenRevokedAndSave(String jwtToken_String){
        JwtToken jwtToken = jwtTokenRepository.findByToken(jwtToken_String).orElseThrow(() ->
                new BadRequestException("Invalid JWT Token"));
        jwtToken.set_revoked(true);
        jwtTokenRepository.save(jwtToken);
    }

    private void clearAuthentication(HttpServletResponse response){
        CookieUtils.deleteCookie(response, jwtTokenCookieName);
        SecurityContextHolder.clearContext();
    }

    public ResponseEntity<GetUserResponseDTO> getUser(HttpServletRequest request) {
        String jwt_token = getJwtTokenFromRequest(request);
        String email = jwtService.extractEmail(jwt_token);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new BadRequestException("Bad Request"));
        return ResponseEntity.ok(GetUserResponseDTO.builder()
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build());
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

    private String getJwtTokenFromRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        return CookieUtils.getCookieValue(cookies, jwtTokenCookieName).orElseThrow(
                () -> new BadRequestException("Sorry. You're not Authenticated")
        );
    }
}

package com.zemka.movieservice.configuration;

import com.zemka.movieservice.model.entity.User;
import com.zemka.movieservice.repository.UserRepository;
import com.zemka.movieservice.service.JwtService;
import com.zemka.movieservice.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    @Value("${jwt_token_cookie_name}")
    private String jwtTokenCookieName;
    @Value("${jwt_token_lifetime}")
    private Integer jwtTokenLifeTime;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return; // TODO LOGGER
        }
        setAuthenticationAndSetCookies(authentication, response);
        getRedirectStrategy().sendRedirect(request, response, "/");
    }

    private void setAuthenticationAndSetCookies(Authentication authentication, HttpServletResponse response){
        String jwt_token = makeJwtToken(authentication);
        CookieUtils.addCookie(response, jwtTokenCookieName, jwt_token, jwtTokenLifeTime);
    }

    private String makeJwtToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        user = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        jwtService.saveToken(jwtToken, user);
        return jwtToken;
    }
}

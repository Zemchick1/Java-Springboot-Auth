package com.zemka.graphicscardservice.configuration;

import com.zemka.graphicscardservice.service.JwtService;
import com.zemka.graphicscardservice.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt_token_cookie_name}")
    private String jwtTokenCookieName;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String jwt_token = CookieUtils.getCookieValue(cookies, jwtTokenCookieName).orElse(null);
        if (cookies == null || jwt_token == null){
            filterChain.doFilter(request, response);
            return;
        }
        String email;
        try {
            email = jwtService.extractEmail(jwt_token);
        } catch (RuntimeException exception){
            CookieUtils.deleteCookie(response, jwtTokenCookieName);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Authentication Token is expired or not valid, Please Log In again");
            return;
        }
        if (email != null){
            if (SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
                if(jwtService.isTokenValid(jwt_token, userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                else {
                    CookieUtils.deleteCookie(response, jwtTokenCookieName);
                }
            }
        }
        else {
            CookieUtils.deleteCookie(response, jwtTokenCookieName);
        }
        filterChain.doFilter(request, response);
    }
}

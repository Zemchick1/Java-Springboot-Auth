package com.zemka.movieservice.configuration;

import com.zemka.movieservice.service.CustomOAuth2Service;
import com.zemka.movieservice.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final LogoutService logoutService;
    private final CustomOAuth2Service customOAuth2Service;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(httpRequests
                        -> httpRequests
                        .requestMatchers("/api/auth/**", "/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sessionManagement
                        -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logoutConfigurer
                        -> logoutConfigurer
                        .addLogoutHandler(logoutService)
                        .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
                        .logoutUrl("/api/auth/logout"))
                .oauth2Login(oauth2 ->
                        oauth2 // TODO to implement state Cookies, Security Reason
                                .authorizationEndpoint(authorizationEndpointConfig
                                        -> authorizationEndpointConfig
                                        .baseUri("/oauth2/authorize"))
                                .redirectionEndpoint(redirectionEndpointConfig
                                        -> redirectionEndpointConfig
                                        .baseUri("/oauth2/callback/*"))
                                .userInfoEndpoint(userInfoEndpointConfig
                                        -> userInfoEndpointConfig
                                        .userService(customOAuth2Service))
                                .successHandler(customOAuth2SuccessHandler)
                                .failureHandler(customOAuth2FailureHandler));
        return http.build();
    }
}

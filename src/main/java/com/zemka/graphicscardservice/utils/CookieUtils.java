package com.zemka.graphicscardservice.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class CookieUtils {
    @Value("${website_domain}")
    private static String website_domain;
    public static Optional<String> getCookieValue(Cookie[] cookies, String name) {
        if (cookies != null && cookies.length > 0) {
            return Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(name))
                    .findFirst()
                    .map(Cookie::getValue);
        }
        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
//                .secure(true)
                .domain(website_domain)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name)
                .httpOnly(true)
//                .secure(true)
                .domain(website_domain)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}

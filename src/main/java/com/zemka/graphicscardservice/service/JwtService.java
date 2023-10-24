package com.zemka.graphicscardservice.service;

import com.zemka.graphicscardservice.model.entity.JwtToken;
import com.zemka.graphicscardservice.model.entity.User;
import com.zemka.graphicscardservice.repository.JwtTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt_token_lifetime}")
    private Long jwt_token_lifetime;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final JwtTokenRepository jwtTokenRepository;

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwt_token_lifetime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractEmail(String jwt_token){
        return extractClaim(jwt_token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token) // TODO EXCEPTION
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        boolean isNotRevoked = jwtTokenRepository.findByToken(token)
                .map(t -> !t.is_revoked())
                .orElse(false);
        String email = extractEmail(token);
        boolean isNameEquals = email.equals(userDetails.getUsername());
        return isNotRevoked && isNameEquals;
    }

    public void saveToken(String jwt_token, User user) {
        JwtToken jwtToken = JwtToken.builder()
                .token(jwt_token)
                .user(user)
                .build();
        jwtTokenRepository.save(jwtToken);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

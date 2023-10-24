package com.zemka.graphicscardservice.repository;

import com.zemka.graphicscardservice.model.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Integer> {
    Optional<JwtToken> findByToken(String token);
}

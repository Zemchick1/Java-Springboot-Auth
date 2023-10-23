package com.zemka.graphicscardservice.repository;

import com.zemka.graphicscardservice.model.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Integer> {
}

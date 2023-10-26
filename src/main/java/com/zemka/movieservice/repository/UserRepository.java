package com.zemka.movieservice.repository;

import com.zemka.movieservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    int countByEmail(String email);
}

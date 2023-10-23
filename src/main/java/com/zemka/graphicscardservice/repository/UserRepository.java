package com.zemka.graphicscardservice.repository;

import com.zemka.graphicscardservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    int countByEmail(String email);
}

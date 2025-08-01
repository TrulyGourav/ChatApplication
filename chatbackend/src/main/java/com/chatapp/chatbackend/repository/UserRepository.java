package com.chatapp.chatbackend.repository;

import java.util.Optional;

import com.chatapp.chatbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);

}

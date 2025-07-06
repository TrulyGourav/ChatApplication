package com.chatapp.chatbackend.service;

import com.chatapp.chatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public String generateUniqueUsername() {
        String username;
        do {
            username = "user" + UUID.randomUUID().toString().substring(0, 8);
        } while (userRepo.existsByUsername(username));
        return username;
    }
}

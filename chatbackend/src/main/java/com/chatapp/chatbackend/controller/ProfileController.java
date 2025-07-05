package com.chatapp.chatbackend.controller;

import com.chatapp.chatbackend.model.User;
import com.chatapp.chatbackend.repository.UserRepository;
import com.chatapp.chatbackend.service.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
        import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping("/get")
    public ResponseEntity<?> getProfile() {
        String email = AuthUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount() {
        String email = AuthUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok("Account deleted successfully");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }
}

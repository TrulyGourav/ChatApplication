package com.chatapp.chatbackend.controller;

import com.chatapp.chatbackend.model.AuthRequest;
import com.chatapp.chatbackend.model.Role;
import com.chatapp.chatbackend.model.User;
import com.chatapp.chatbackend.repository.UserRepository;
import com.chatapp.chatbackend.security.JwtService;
import com.chatapp.chatbackend.service.AuthUtil;
import com.chatapp.chatbackend.service.ChatRoomService;
import com.chatapp.chatbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ChatRoomService chatRoomService;
    private final com.chatapp.chatbackend.service.UserDetailsServiceImpl userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        // Generate a unique username
        String generatedUsername = userService.generateUniqueUsername();
        user.setUsername(generatedUsername);

        userRepo.save(user);
        return ResponseEntity.ok("User registered with username: " + generatedUsername);
    }


    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        return jwtService.generateToken(userDetails);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        String email = AuthUtil.getCurrentUserEmail();

        // Clean up WebSocket/room state
//        chatRoomService.removeUserFromAnyRoom(email);
//        userSessionRegistry.removeUser(email); // optional if you track by email

        return ResponseEntity.ok("User signed out successfully.");
    }
}

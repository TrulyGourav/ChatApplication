package com.chatapp.chatbackend.model;

import lombok.*;

@Data
public class AuthRequest {
    private String email;
    private String password;
}

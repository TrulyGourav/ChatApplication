package com.chatapp.chatbackend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication auth: " + auth);
        return auth.getName(); // email is set as username in our JWT
    }
}

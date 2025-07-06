package com.chatapp.chatbackend.model;

import lombok.*;

// This is a DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusMessage {
    private String user;
    private String status; // "ONLINE" or "OFFLINE"
}

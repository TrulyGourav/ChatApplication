package com.chatapp.chatbackend.model;

import lombok.*;

// This is a DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequest {
    private JoinType type;
    private String roomId; // Optional unless type == SPECIFIC
}

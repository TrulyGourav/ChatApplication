package com.chatapp.chatbackend.model;


import lombok.*;

// This is a DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String sender;
    private String content;
    private String roomId;
}

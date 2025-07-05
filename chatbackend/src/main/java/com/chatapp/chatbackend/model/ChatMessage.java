package com.chatapp.chatbackend.model;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String sender;
    private String content;
    private String roomId;
}

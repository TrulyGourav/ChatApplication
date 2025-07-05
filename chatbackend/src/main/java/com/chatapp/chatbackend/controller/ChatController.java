package com.chatapp.chatbackend.controller;

import com.chatapp.chatbackend.model.ChatMessage;
import com.chatapp.chatbackend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/message")
    public void handleMessage(@Payload ChatMessage message) {
        if (chatRoomService.getUsersInRoom(message.getRoomId()).size() <= 2) {
            messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
        }
    }
}

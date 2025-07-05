package com.chatapp.chatbackend.controller;

import com.chatapp.chatbackend.model.ChatMessage;
import com.chatapp.chatbackend.model.ChatMessageEntity;
import com.chatapp.chatbackend.repository.ChatMessageRepository;
import com.chatapp.chatbackend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/message")
    public void handleMessage(@Payload ChatMessage message) {
        if (chatRoomService.getUsersInRoom(message.getRoomId()).size() <= 2) {
            // 1. Save message to DB
            ChatMessageEntity entity = ChatMessageEntity.builder()
                    .sender(message.getSender())
                    .content(message.getContent())
                    .roomId(message.getRoomId())
                    .timestamp(LocalDateTime.now())
                    .build();

            chatMessageRepository.save(entity);

            // 2. Broadcast to WebSocket
            messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
        }
    }

}

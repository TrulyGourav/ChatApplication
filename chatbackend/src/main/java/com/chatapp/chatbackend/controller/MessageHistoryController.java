package com.chatapp.chatbackend.controller;

import com.chatapp.chatbackend.model.ChatMessageEntity;
import com.chatapp.chatbackend.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageHistoryController {

    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/{roomId}")
    public List<ChatMessageEntity> getMessages(@PathVariable String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}

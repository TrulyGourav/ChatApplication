package com.chatapp.chatbackend.controller;


import com.chatapp.chatbackend.service.ChatRoomService;
import com.chatapp.chatbackend.service.AuthUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
        import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class RoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/join/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId, HttpSession session) {
        String email = AuthUtil.getCurrentUserEmail();
        String sessionId = session.getId();

        boolean success = chatRoomService.joinRoom(roomId, email, sessionId);
        if (!success)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Room is full or user already joined");

        return ResponseEntity.ok("Joined room " + roomId);
    }

    @PostMapping("/leave/{roomId}")
    public ResponseEntity<?> leaveRoom(@PathVariable String roomId, HttpSession session) {
        String email = AuthUtil.getCurrentUserEmail();
        String sessionId = session.getId();

        chatRoomService.leaveRoom(roomId, email, sessionId);
        return ResponseEntity.ok("Left room " + roomId);
    }
}

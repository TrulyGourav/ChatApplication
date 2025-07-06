package com.chatapp.chatbackend.controller;


import com.chatapp.chatbackend.model.JoinRequest;
import com.chatapp.chatbackend.service.ChatRoomService;
import com.chatapp.chatbackend.service.AuthUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
        import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class RoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody JoinRequest request, HttpSession session) {
        String email = AuthUtil.getCurrentUserEmail();
        String sessionId = session.getId();

        switch (request.getType()) {
            case CREATE -> {
                String newRoomId = UUID.randomUUID().toString().substring(0, 8);
                boolean joined = chatRoomService.joinRoom(newRoomId, email, sessionId, true);
                return ResponseEntity.ok(newRoomId);
            }

            case RANDOM -> {
                String availableRoom = chatRoomService.getAnyAvailableRoom();
                if (availableRoom == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available room to join");
                }
                boolean joined = chatRoomService.joinRoom(availableRoom, email, sessionId, false);
                return ResponseEntity.ok(availableRoom);
            }

            case SPECIFIC -> {
                if (request.getRoomId() == null || request.getRoomId().isBlank()) {
                    return ResponseEntity.badRequest().body("Room ID is required for SPECIFIC join type");
                }
                if (!chatRoomService.isActiveAvailableRoom(request.getRoomId())){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No room found with ID: " + request.getRoomId());
                }
                boolean joined = chatRoomService.joinRoom(request.getRoomId(), email, sessionId, false);
                if (!joined) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Room is full or error joining");
                return ResponseEntity.ok(request.getRoomId());
            }

            default -> throw new IllegalArgumentException("Invalid join type");
        }
    }


    @PostMapping("/leave/{roomId}")
    public ResponseEntity<?> leaveRoom(@PathVariable String roomId, HttpSession session) {
        String email = AuthUtil.getCurrentUserEmail();
        String sessionId = session.getId();

        chatRoomService.leaveRoom(roomId, email, sessionId);
        return ResponseEntity.ok("Left room " + roomId);
    }
}

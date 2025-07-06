package com.chatapp.chatbackend.service;

import com.chatapp.chatbackend.model.UserStatusMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final SimpMessagingTemplate messagingTemplate;

    // roomId → Set of usernames
    private final Map<String, Set<String>> roomUserMap = new HashMap<>();
    private final Map<String, String> roomOwnerMap = new HashMap<>();
    // sessionId → (roomId, username)
    private final Map<String, UserSession> sessionMap = new HashMap<>();

    public synchronized boolean joinRoom(String roomId, String username, String sessionId, boolean isOwner) {
        roomUserMap.putIfAbsent(roomId, new HashSet<>());
        Set<String> users = roomUserMap.get(roomId);

        if (users.size() >= 2) return false;

        boolean added = users.add(username);
        if (added) {
            sessionMap.put(sessionId, new UserSession(roomId, username));
            if (isOwner) roomOwnerMap.put(roomId, username);
            sendStatus(roomId, username, "ONLINE");
        }

        return added;
    }

    public synchronized void leaveRoom(String roomId, String username, String sessionId) {
        Set<String> users = roomUserMap.get(roomId);
        if (users != null) {
            users.remove(username);
            if (users.isEmpty() || isOwner(roomId, username)) {
                // Cleanup room if empty or owner left
                roomUserMap.remove(roomId);
                roomOwnerMap.remove(roomId);
            }
        }
        sessionMap.remove(sessionId);
        sendStatus(roomId, username, "OFFLINE");
    }

    public synchronized void handleDisconnect(String sessionId) {
        UserSession session = sessionMap.get(sessionId);
        if (session != null) {
            leaveRoom(session.roomId(), session.username(), sessionId);
        }
    }

    private void sendStatus(String roomId, String username, String status) {
        UserStatusMessage statusMsg = new UserStatusMessage(username, status);
        messagingTemplate.convertAndSend("/topic/" + roomId + "/status", statusMsg);
    }

    public synchronized String getAnyAvailableRoom() {
        return roomUserMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() < 2)
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }

    public synchronized boolean isActiveAvailableRoom(String roomId) {
        return roomUserMap.containsKey(roomId) && roomUserMap.get(roomId).size() == 1;
    }

    private boolean isOwner(String roomId, String username) {
        return username.equals(roomOwnerMap.get(roomId));
    }

    public synchronized boolean isRoomFull(String roomId) {
        return roomUserMap.getOrDefault(roomId, new HashSet<>()).size() >= 2;
    }

//    public Set<String> getUsersInRoom(String roomId) {
//        return roomUserMap.getOrDefault(roomId, new HashSet<>());
//    }

    public synchronized Set<String> getUsersInRoom(String roomId) {
        return new HashSet<>(roomUserMap.getOrDefault(roomId, Set.of()));
    }

    public record UserSession(String roomId, String username) {}
}

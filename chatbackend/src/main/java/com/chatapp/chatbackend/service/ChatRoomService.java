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
    // username -> roomId (temporary marker after /join)
    private final Map<String, String> pendingSessionUsers = new HashMap<>();


    public synchronized boolean joinRoom(String roomId, String username, String sessionId, boolean isOwner) {
        System.out.println("-- ENTERED JOIN ROOM --");
        roomUserMap.putIfAbsent(roomId, new HashSet<>());
        Set<String> users = roomUserMap.get(roomId);

        if (users.size() >= 2) return false;

        boolean added = users.add(username);
        if (added) {
//            System.out.println("Adding session to sessionMap...");
//            sessionMap.put(sessionId, new UserSession(roomId, username));
//            System.out.println("sessionMap after adding: "+sessionMap);
            pendingSessionUsers.put(username, roomId);
            if (isOwner) {
                System.out.println("Adding as owner to roomOwnerMap...");
                roomOwnerMap.put(roomId, username);
            }
            sendStatus(roomId, username, "ONLINE");
        }
        System.out.println(username+ " joined "+roomId);
        System.out.println("No. of person in "+ roomId + ": " + users.size());

        System.out.println("roomUserMap: "+roomUserMap);
        System.out.println("-- EXITED JOIN ROOM --");
        return added;
    }

    public synchronized void leaveRoom(String roomId, String username, String sessionId) {
        System.out.println("-- ENTERED LEAVE ROOM --");
        Set<String> users = roomUserMap.get(roomId);
        System.out.println("users: " + users);
        if (users != null) {
            users.remove(username);
            System.out.println(username+ " left "+roomId);
            System.out.println("No. of person in "+ roomId + ": " + users.size());
            if (users.isEmpty() || isOwner(roomId, username)) {
                // Cleanup room if empty or owner left
                roomUserMap.remove(roomId);
                roomOwnerMap.remove(roomId);
                System.out.println("Cleaned the room....");
            }
        }
//        System.out.println("sessionId (not from websocket): "+sessionId);
//        sessionMap.remove(sessionId);
//        --> Removing from sessionMap takes place via disconnectEvent

        sendStatus(roomId, username, "OFFLINE");
        System.out.println("updated roomUserMap: "+roomUserMap);
        System.out.println("-- EXITING LEAVE ROOM --");
    }

    public synchronized void handleDisconnect(String sessionId) {
        System.out.println("-- ENTERED handleDisconnect --");
        UserSession session = sessionMap.get(sessionId);
        if (session != null) {
            sessionMap.remove(sessionId);
            System.out.println("Removed WebSocket session: " + sessionId);
        }
        System.out.println("updated sessionMap: "+sessionMap);
        System.out.println("-- EXITED handleDisconnect --");
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

    public synchronized void trackWebSocketSession(String sessionId, String roomId, String username) {
        String expectedRoom = pendingSessionUsers.get(username);
        if (expectedRoom == null || !expectedRoom.equals(roomId)) {
            System.out.println("❌ User tried to connect without joining properly: " + username);
            return;
        }
        // ✅ User was approved previously by joinRoom()
        sessionMap.put(sessionId, new UserSession(roomId, username));
        pendingSessionUsers.remove(username); // cleanup
        System.out.println("✅ Added WebSocket session: " + sessionId);
        System.out.println("🔐 sessionMap: " + sessionMap);
    }


    public synchronized Set<String> getUsersInRoom(String roomId) {
        return new HashSet<>(roomUserMap.getOrDefault(roomId, Set.of()));
    }

    public record UserSession(String roomId, String username) {}
}

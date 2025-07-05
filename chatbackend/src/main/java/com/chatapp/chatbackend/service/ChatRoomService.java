package com.chatapp.chatbackend.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatRoomService {

    private final Map<String, Set<String>> roomUserMap = new HashMap<>();

    public boolean joinRoom(String roomId, String username) {
        roomUserMap.putIfAbsent(roomId, new HashSet<>());
        Set<String> users = roomUserMap.get(roomId);
        if (users.size() >= 2) return false;
        return users.add(username); // returns false if already added
    }

    public void leaveRoom(String roomId, String username) {
        Set<String> users = roomUserMap.get(roomId);
        if (users != null) {
            users.remove(username);
            if (users.isEmpty()) {
                roomUserMap.remove(roomId);
            }
        }
    }

    public Set<String> getUsersInRoom(String roomId) {
        return roomUserMap.getOrDefault(roomId, new HashSet<>());
    }
}

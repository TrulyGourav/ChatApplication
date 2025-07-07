package com.chatapp.chatbackend.websocket;

import com.chatapp.chatbackend.security.JwtService;
import com.chatapp.chatbackend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChatRoomService chatRoomService;
    private final JwtService jwtService;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        System.out.println("--- ENTER Detected SessionDisconnectEvent ---");
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        System.out.println("sessionId: "+sessionId);
        log.info("WebSocket disconnected: {}", sessionId);

        chatRoomService.handleDisconnect(sessionId);
        System.out.println("--- EXITED Detected SessionDisconnectEvent ---");
    }

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        System.out.println("--- ENTER Detected handleSessionConnect ---");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String email = null;
        String token = accessor.getFirstNativeHeader("token");
        String roomId = accessor.getFirstNativeHeader("roomId");
        if(token != null){
            email = jwtService.extractUsername(token);
        }
        System.out.println("sessionId: "+sessionId);
        System.out.println("accessor: "+accessor);
        System.out.println("email: "+email);
        System.out.println("roomId: "+roomId);

        if (roomId != null && email != null) {
            System.out.println("WebSocket connected: " + sessionId + " → " + email + " in room " + roomId);
            chatRoomService.trackWebSocketSession(sessionId, roomId, email);
        }
        System.out.println("--- EXITED Detected handleSessionConnect ---");
    }

}

import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let stompClient = null;

export const connectSocket = (roomId, onMessageCallback) => {
  console.log("🧪 connectSocket() CALLED with roomId:", roomId);
  const token = localStorage.getItem('token');

  stompClient = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/chat'),
    reconnectDelay: 10000,
    connectHeaders: {
      roomId,
      token, // Backend will use this in nativeHeaders
    },
    onConnect: () => {
      console.log('✅ WebSocket connected');

      // Subscribe to chat room messages
      stompClient.subscribe(`/topic/${roomId}`, (msg) => {
        const body = JSON.parse(msg.body);
        console.log('📩 Received message:', body);
        onMessageCallback(body);
      });
      resolve(); // ✅ Resolves only when connected
    },
    onStompError: (frame) => {
      console.error('💥 STOMP error', frame.headers['message']);
    },
    onWebSocketError: (err) => {
      console.error('💢 WebSocket connection failed', err);
    }
  });

  console.log("📡 Activating STOMP client...");
  stompClient.activate();
};

export const sendMessage = ({ roomId, sender, content }) => {
  console.log("Checking stompClient: ", stompClient);
  if (stompClient && stompClient.connected) {
    console.log("sendMessage method called");
    stompClient.publish({
      destination: '/app/message',
      body: JSON.stringify({ roomId, sender, content }),
    });
    console.log('✅ Message sent:', { roomId, sender, content });
  } else {
    console.log("stompClient not connected");
  }
};

export const disconnectSocket = () => {
  if (stompClient) {
    stompClient.deactivate();
    console.log("🔌 STOMP Disconnected");
  }
};

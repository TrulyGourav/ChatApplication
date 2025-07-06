import { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import { connectSocket, sendMessage, disconnectSocket } from "../socket";
import API from "../api";
import { toast } from 'react-toastify';
import { useNavigate, Link } from 'react-router-dom';

export default function ChatWindow() {
  const { roomId } = useParams();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const chatRef = useRef();
  const userEmail = localStorage.getItem("userEmail"); // Set during login/signup
  const navigate = useNavigate();

  useEffect(() => {
    if (!userEmail) {
      console.warn("No user email found in localStorage");
      return;
    } else {
      console.log("LoggedIn userEmail: ", userEmail);
    }

    // Connect and subscribe
    connectSocket(roomId, (msg) => {
      setMessages((prev) => [...prev, msg]);
    });

    return () => disconnectSocket();
  }, [roomId]);

  useEffect(() => {
    chatRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = () => {
    if (input.trim()) {
      const payload = {
        roomId,
        sender: userEmail,
        content: input,
      };

      sendMessage(payload);
      // setMessages((prev) => [...prev, { sender: 'Me', content: input }]);
      setInput("");
    }
  };

  const handleLeaveRoom = async () => {
    try {
      await API.post(`/chat/leave/${roomId}`);
      toast.success("You have left the room");
      navigate("/rooms");
    } catch (err) {
      toast.error("Failed to leave room");
    }
  };

  return (
    <div
      className="flex flex-col h-screen p-4 bg-cover bg-center"
      style={{ backgroundImage: "url('/images/chat-bg.png')" }}
    >
      <h2 className="text-xl font-bold mb-4 text-center">Room ID: {roomId}</h2>
      <div className="flex-1 overflow-y-auto bg-white rounded shadow p-4 mb-4">
        {messages.map((msg, idx) => {
          const isSelf = msg.sender === userEmail;

          return (
            <div
              key={idx}
              className={`mb-2 ${isSelf ? "text-right" : "text-left"}`}
            >
              <p className="text-sm text-gray-600">
                {isSelf ? "Me" : msg.sender}
              </p>
              <div
                className={`inline-block px-4 py-2 rounded-lg ${
                  isSelf ? "bg-blue-200" : "bg-gray-200"
                }`}
              >
                {msg.content}
              </div>
            </div>
          );
        })}

        <div ref={chatRef}></div>
      </div>

      <div className="flex gap-2">
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") handleSend();
          }}
          className="flex-1 border rounded px-3 py-2 focus:outline-none"
          placeholder="Type your message..."
        />
        <button
          onClick={handleSend}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition"
        >
          Send
        </button>

        {/* Leave button div --> */}
        <div className="flex gap-2">
          <button
            onClick={handleLeaveRoom}
            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition"
          >
            Leave Room
          </button>
        </div>
      </div>
    </div>
  );
}

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';
import { toast } from 'react-toastify';

export default function RoomOptions() {
  const [roomId, setRoomId] = useState('');
  const navigate = useNavigate();

  const joinRequestPayload = {
    type: null,
    roomId: null
  };
  const createRoom = async () => {
    joinRequestPayload.type = 'CREATE';
    const res = await API.post('/chat/join', joinRequestPayload);
    navigate(`/chat/${res.data}`);
  };

  const enterRandomRoom = async () => {
    try {
      joinRequestPayload.type = 'RANDOM';
    const res = await API.post('/chat/join', joinRequestPayload);
    if (res.status === 200) {
      navigate(`/chat/${res.data}`);
    }
    } catch (err) {
      toast.error("No active rooms available");
    }
  };

  const enterSpecificRoom = async () => {
    try {
      if(!roomId || roomId==""){
        toast.error("Bad input: Invalid Room Id");
        return;
      }
      joinRequestPayload.type = "SPECIFIC";
      joinRequestPayload.roomId = roomId;

      const res = await API.post("/chat/join", joinRequestPayload);

      if (res.status === 200) {
        navigate(`/chat/${roomId}`);
      }
    } catch (err) {
      const message =
        err.response?.data?.message || err.message || "Unknown error occurred";
      toast.error(`No room found with Id "${roomId}"`);
    }
  };

  return (
    <div 
      className="flex items-center justify-center h-screen bg-cover bg-center"
      style={{ backgroundImage: "url('/images/chat-bg.png')" }}
    >
      <div className="bg-white shadow-lg rounded-lg p-8 w-96">
        <h2 className="text-2xl font-bold mb-6 text-center">Choose a Room</h2>
        <button
          className="w-full bg-blue-500 text-white py-2 rounded mb-3 hover:bg-blue-600 transition"
          onClick={createRoom}
        >
          Create New Room
        </button>
        <button
          className="w-full bg-purple-500 text-white py-2 rounded mb-3 hover:bg-purple-600 transition"
          onClick={enterRandomRoom}
        >
          Enter Random Room
        </button>
        <input
          className="w-full border rounded px-3 py-2 mb-3"
          placeholder="Enter Room ID"
          onChange={e => setRoomId(e.target.value)}
        />
        <button
          className="w-full bg-green-500 text-white py-2 rounded hover:bg-green-600 transition"
          onClick={enterSpecificRoom}
        >
          Join Specific Room
        </button>
      </div>
    </div>
  );
}
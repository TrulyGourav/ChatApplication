import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import Signup from "./components/Signup";
import RoomOptions from "./components/RoomOptions";
import ChatWindow from "./components/ChatWindow";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function App() {
  return (
    <>
      <Router>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/rooms" element={<RoomOptions />} />
          <Route path="/chat/:roomId" element={<ChatWindow />} />
        </Routes>
      </Router>
      <ToastContainer
        position="top-center"
        autoClose={3000} // 3 seconds
        pauseOnFocusLoss={false} // Don't pause on tab/window blur
        pauseOnHover={false} // Don't pause when hovering
        closeOnClick
        draggable
      />
    </>
  );
}

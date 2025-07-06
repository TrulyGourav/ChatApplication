import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import API from '../api';
import { toast } from 'react-toastify';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const login = async () => {
    if (!email || !password) return toast.error('All fields are required.');
    try {
      const res = await API.post('/auth/login', { email, password });
      console.log("res: ", res);
      localStorage.setItem('token', res.data);
      localStorage.setItem('userEmail', email);
      toast.success('Login successful!');
      navigate('/rooms');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Login failed');
    }
  };

  return (
    <div 
      className="flex items-center justify-center h-screen bg-cover bg-center"
      style={{ backgroundImage: "url('/images/chat-bg.png')" }}
    >
      <div className="bg-white shadow-md rounded-lg p-8 w-96">
        <h2 className="text-2xl font-bold mb-6 text-center">Login</h2>
        <input
          className="w-full border rounded px-3 py-2 mb-4 focus:outline-none focus:ring focus:border-blue-400"
          placeholder="Email"
          onChange={e => setEmail(e.target.value)}
        />
        <input
          type="password"
          className="w-full border rounded px-3 py-2 mb-4 focus:outline-none focus:ring focus:border-blue-400"
          placeholder="Password"
          onChange={e => setPassword(e.target.value)}
        />
        <button
          className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 transition"
          onClick={login}
        >
          Login
        </button>
        <p className="text-center mt-4">
          Don’t have an account?{' '}
          <Link className="text-blue-500 hover:underline" to="/signup">Signup</Link>
        </p>
      </div>
    </div>
  );
}
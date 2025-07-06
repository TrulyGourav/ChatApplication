import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import API from '../api';

export default function Signup() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const validate = () => {
    if (!email || !password) {
      toast.error('Email and password are required.');
      return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      toast.error('Invalid email format.');
      return false;
    }
    if (password.length < 6) {
      toast.error('Password must be at least 6 characters.');
      return false;
    }
    return true;
  };

  const signup = async () => {
    if (!validate()) return;
    try {
      await API.post('/auth/register', { email, password });
      toast.success('Signup successful. Redirecting...');
      setTimeout(() => navigate('/'), 1500);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Signup failed');
    }
  };

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <div className="bg-white shadow-md rounded-lg p-8 w-96">
        <h2 className="text-2xl font-bold mb-6 text-center">Signup</h2>
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
          className="w-full bg-green-500 text-white py-2 rounded hover:bg-green-600 transition"
          onClick={signup}
        >
          Register
        </button>
        <p className="text-center mt-4">
          Already have an account?{' '}
          <Link className="text-blue-500 hover:underline" to="/">Login</Link>
        </p>
      </div>
    </div>
  );
}
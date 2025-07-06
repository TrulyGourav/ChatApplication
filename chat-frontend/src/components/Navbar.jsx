import { useNavigate, useLocation } from 'react-router-dom';
import { useEffect, useState } from 'react';

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [userEmail, setUserEmail] = useState('');

  useEffect(() => {
    setUserEmail(localStorage.getItem('userEmail'));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    navigate('/');
  };

  const hideNavbarOn = ['/', '/signup'];
  if (hideNavbarOn.includes(location.pathname)) return null;

  return (
    <div className="w-full bg-white shadow px-6 py-3 flex justify-between items-center fixed top-0 z-50">
      <div className="text-xl font-bold text-blue-600">HeyTime</div>
      <div className="flex items-center space-x-4">
        <span className="text-gray-700">{userEmail}</span>
        <button
          onClick={handleLogout}
          className="bg-red-500 hover:bg-red-600 text-white px-4 py-1 rounded"
        >
          Sign Out
        </button>
      </div>
    </div>
  );
}

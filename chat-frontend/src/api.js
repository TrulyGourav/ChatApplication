// src/api.js
import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// ✅ Only attach token for NON-auth routes
API.interceptors.request.use(config => {
  const authPaths = ['/auth/login', '/auth/signup'];

  if (authPaths.includes(config.url)) {
    // ⛔ skip attaching token
    delete config.headers.Authorization;
    return config;
  }

  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    delete config.headers.Authorization;
  }

  return config;
});

export default API;

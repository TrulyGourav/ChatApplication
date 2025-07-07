// src/api.js
import axios from 'axios';
import config from './config';

const API = axios.create({
  baseURL: `${config.BASE_URL}/api`,
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

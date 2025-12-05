import axios from 'axios';

// Base URL — prefer environment variable, fallback to localhost dev server
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const TOKEN_KEY = 'ds_token';

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// attach token
api.interceptors.request.use(cfg => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
});

// basic response interceptor to forward errors
api.interceptors.response.use(
  r => r,
  err => {
    // if 401 -> remove token (client handles logout)
    if (err.response && err.response.status === 401) {
      localStorage.removeItem(TOKEN_KEY);
    }
    return Promise.reject(err);
  }
);

// Auth
export const auth = {
  login: (email, password) =>
    api.post('/api/auth/login', { email, password }).then(r => r.data),
  register: (payload) =>
    api.post('/api/auth/register', payload).then(r => r.data)
};

// User
export const user = {
  getMe: () => api.get('/api/user/me').then(r => r.data),
  update: (payload) => api.put('/api/user/me', payload).then(r => r.data)
};

// Child
export const child = {
  get: () => api.get('/api/child').then(r => r.data),
  update: (payload) => api.put('/api/child', payload).then(r => r.data),
  journal: () => api.get('/api/child/journal').then(r => r.data)
};

// Lessons
export const lessons = {
  upcoming: (limit = 3) => api.get('/api/lessons/upcoming', { params: { limit } }).then(r => r.data),
  week: () => api.get('/api/lessons/week').then(r => r.data),
  available: (courseId) => api.get('/api/lessons/available', { params: { courseId } }).then(r => r.data),
  cancel: (id) => api.post(`/api/lessons/${id}/cancel`).then(r => r.data),
  reschedule: (fromLessonId, toLessonId) => api.post('/api/lessons/reschedule', { fromLessonId, toLessonId }).then(r => r.data),
  restore: (missedLessonId, targetLessonId) => api.post('/api/lessons/restore', { missedLessonId, targetLessonId }).then(r => r.data),
  // teacher endpoints
  todayForTeacher: () => api.get('/api/lessons/today').then(r => r.data),
  markAttendance: (id, payload) => api.post(`/api/lessons/${id}/attendance`, payload).then(r => r.data)
};

// Payments
export const payments = {
  list: (page = 0, size = 20) => api.get('/api/payments', { params: { page, size } }).then(r => r.data),
  create: (payload) => api.post('/api/payments', payload).then(r => r.data),
  prices: () => api.get('/api/payments/prices').then(r => r.data)
};

// Notifications
export const notifications = {
  list: (page = 0, size = 20) => api.get('/api/notifications', { params: { page, size } }).then(r => r.data),
  unreadCount: () => api.get('/api/notifications/unread').then(r => r.data),
  markRead: (id) => api.post(`/api/notifications/${id}/read`).then(r => r.data),
  markAllRead: () => api.post('/api/notifications/read-all').then(r => r.data)
};

export const TOKEN = {
  KEY: TOKEN_KEY,
  set: (token) => localStorage.setItem(TOKEN_KEY, token),
  remove: () => localStorage.removeItem(TOKEN_KEY),
  get: () => localStorage.getItem(TOKEN_KEY)
};

export default api;


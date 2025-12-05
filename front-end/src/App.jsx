import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './views/auth/Login';
import Register from './views/auth/Register';
import Dashboard from './views/pages/Dashboard';
import Profile from './views/pages/Profile';
import Child from './views/pages/Child';
import Lessons from './views/pages/Lessons';
import Payments from './views/pages/Payments';
import Landing from './views/pages/Landing';
import Notifications from './views/pages/Notifications';
import Navbar from './views/components/Navbar';
import ProtectedRoute from './views/components/ProtectedRoute';
import { useAuth } from './auth/AuthProvider';

export default function App() {
  const { loading } = useAuth();

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      <Navbar />
      <main style={{ padding: 16 }}>
        <Routes>
          <Route path="/" element={<Landing />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
          <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
          <Route path="/child" element={<ProtectedRoute><Child /></ProtectedRoute>} />
          <Route path="/lessons" element={<ProtectedRoute><Lessons /></ProtectedRoute>} />
          <Route path="/payments" element={<ProtectedRoute><Payments /></ProtectedRoute>} />
          <Route path="/notifications" element={<ProtectedRoute><Notifications /></ProtectedRoute>} />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
}


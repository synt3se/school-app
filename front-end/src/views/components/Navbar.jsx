import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthProvider';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav style={{ padding: 12, borderBottom: '1px solid #ddd' }}>
      <Link to="/">Home</Link> {' | '}
      {user && <>
        <Link to="/dashboard">Dashboard</Link> {' | '}
        <Link to="/profile">Profile</Link> {' | '}
        <Link to="/child">Child</Link> {' | '}
        <Link to="/lessons">Lessons</Link> {' | '}
        <Link to="/payments">Payments</Link> {' | '}
        <Link to="/notifications">Notifications</Link> {' | '}
        <button onClick={handleLogout}>Logout</button>
      </>}
      {!user && <>
        <Link to="/login">Login</Link> {' | '}
        <Link to="/register">Register</Link>
      </>}
    </nav>
  );
}


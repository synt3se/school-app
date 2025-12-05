import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthProvider';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      const msg = err?.response?.data?.message || 'Login failed';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={submit}>
        <div>
          <label>Email</label><br />
          <input value={email} onChange={e => setEmail(e.target.value)} type="email" required />
        </div>
        <div>
          <label>Password</label><br />
          <input value={password} onChange={e => setPassword(e.target.value)} type="password" required />
        </div>
        <div>
          <button type="submit" disabled={loading}>{loading ? '...' : 'Login'}</button>
        </div>
        {error && <div style={{ color: 'red' }}>{error}</div>}
      </form>
    </div>
  );
}


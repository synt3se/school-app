import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthProvider';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    child: { fullName: '', birthDate: '' },
    branchId: ''
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const update = (path, value) => {
    if (path.startsWith('child.')) {
      setForm(f => ({ ...f, child: { ...f.child, [path.split('.')[1]]: value } }));
    } else {
      setForm(f => ({ ...f, [path]: value }));
    }
  };

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await register(form);
      // after register, typically user must login -> redirect to login
      navigate('/login');
    } catch (err) {
      setError(err?.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Register (Parent)</h2>
      <form onSubmit={submit}>
        <div>
          <label>Full name</label><br />
          <input value={form.fullName} onChange={e => update('fullName', e.target.value)} required />
        </div>
        <div>
          <label>Email</label><br />
          <input type="email" value={form.email} onChange={e => update('email', e.target.value)} required />
        </div>
        <div>
          <label>Phone</label><br />
          <input value={form.phone} onChange={e => update('phone', e.target.value)} required />
        </div>
        <div>
          <label>Password</label><br />
          <input type="password" value={form.password} onChange={e => update('password', e.target.value)} required />
        </div>

        <h4>Child</h4>
        <div>
          <label>Child full name</label><br />
          <input value={form.child.fullName} onChange={e => update('child.fullName', e.target.value)} required />
        </div>
        <div>
          <label>Child birthDate (YYYY-MM-DD)</label><br />
          <input type="date" value={form.child.birthDate} onChange={e => update('child.birthDate', e.target.value)} required />
        </div>

        <div>
          <label>Branch ID (UUID)</label><br />
          <input value={form.branchId} onChange={e => update('branchId', e.target.value)} required />
        </div>

        <div>
          <button type="submit" disabled={loading}>{loading ? '...' : 'Register'}</button>
        </div>
        {error && <div style={{ color: 'red' }}>{error}</div>}
      </form>
    </div>
  );
}


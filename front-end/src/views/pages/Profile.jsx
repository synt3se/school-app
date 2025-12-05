import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/AuthProvider';
import { useMVC } from '../../controllers/MVCContext';

export default function Profile() {
  const { user } = useAuth();
  const { actions } = useMVC();
  const [form, setForm] = useState({ fullName: '', phone: '' });
  const [status, setStatus] = useState(null);

  // Initialize form with user data
  useEffect(() => {
    if (user) {
      setForm({
        fullName: user.fullName || '',
        phone: user.phone || ''
      });
    }
  }, [user]);

  const save = async (e) => {
    e.preventDefault();
    setStatus('saving');

    // Validate form data
    const validation = actions.user.validateUserData(form);
    if (!validation.isValid) {
      setStatus(`Validation error: ${validation.error}`);
      return;
    }

    try {
      await actions.user.updateCurrentUser(form);
      setStatus('saved');
    } catch (err) {
      setStatus(err?.response?.data?.message || 'error');
    }
  };

  return (
    <div>
      <h2>Profile</h2>
      <form onSubmit={save}>
        <div>
          <label>Full name</label><br />
          <input
            value={form.fullName}
            onChange={e => setForm(f => ({ ...f, fullName: e.target.value }))}
            required
          />
        </div>
        <div>
          <label>Phone</label><br />
          <input
            value={form.phone}
            onChange={e => setForm(f => ({ ...f, phone: e.target.value }))}
          />
        </div>
        <div>
          <button type="submit" disabled={status === 'saving'}>
            {status === 'saving' ? 'Saving...' : 'Save'}
          </button>
        </div>
        {status && status !== 'saving' && (
          <div style={{ color: status === 'saved' ? 'green' : 'red' }}>
            {status}
          </div>
        )}
      </form>
    </div>
  );
}


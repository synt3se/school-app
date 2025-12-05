import React, { useEffect, useState } from 'react';
import { useMVC } from '../../controllers/MVCContext';

export default function Child() {
  const { state, actions } = useMVC();
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({ fullName: '', birthDate: '' });

  const child = state.data.child;
  const loading = state.loading.child;

  // Initialize form with child data
  useEffect(() => {
    if (child) {
      setForm({
        fullName: child.fullName || '',
        birthDate: child.birthDate || ''
      });
    }
  }, [child]);

  // Load child data on mount
  useEffect(() => {
    if (!child && !loading) {
      actions.child.getCurrentChild().catch(console.error);
    }
  }, [child, loading, actions.child]);

  const save = async (e) => {
    e.preventDefault();

    // Validate form data
    const validation = actions.child.validateChildData(form);
    if (!validation.isValid) {
      console.error('Validation error:', validation.error);
      return;
    }

    try {
      await actions.child.updateCurrentChild(form);
      setEditing(false);
    } catch (err) {
      console.error('Error updating child:', err);
    }
  };

  if (loading) return <div>Loading child...</div>;
  if (!child) return <div>No child data found</div>;

  const age = actions.child.calculateAge(child);

  return (
    <div>
      <h2>Child</h2>
      {!editing ? (
        <div>
          <div>Full name: {child.fullName}</div>
          <div>Birth date: {child.birthDate}</div>
          <div>Age: {age || 'Unknown'}</div>
          <button onClick={() => setEditing(true)}>Edit</button>
        </div>
      ) : (
        <form onSubmit={save}>
          <div>
            <label>Name</label><br />
            <input
              value={form.fullName}
              onChange={e => setForm(f => ({ ...f, fullName: e.target.value }))}
              required
            />
          </div>
          <div>
            <label>Birth date</label><br />
            <input
              type="date"
              value={form.birthDate}
              onChange={e => setForm(f => ({ ...f, birthDate: e.target.value }))}
              required
            />
          </div>
          <button type="submit">Save</button>
          <button type="button" onClick={() => setEditing(false)}>Cancel</button>
        </form>
      )}

      <section>
        <h3>Journal</h3>
        <Journal />
      </section>
    </div>
  );
}

function Journal() {
  const { state, actions } = useMVC();
  const [journalEntries, setJournalEntries] = useState(null);

  useEffect(() => {
    const loadJournal = async () => {
      try {
        const entries = await actions.child.getChildJournal();
        setJournalEntries(entries);
      } catch (error) {
        console.error('Error loading journal:', error);
      }
    };

    loadJournal();
  }, [actions.child]);

  if (!journalEntries) return <div>Loading journal...</div>;

  return (
    <ul>
      {journalEntries.map(e => (
        <li key={e.id}>
          {e.date} — {e.project} — total: {e.totalScore}
        </li>
      ))}
    </ul>
  );
}


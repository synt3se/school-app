import React, { useEffect } from 'react';
import { useMVC } from '../../controllers/MVCContext';
import { useAuth } from '../../auth/AuthProvider';

export default function Lessons() {
  const { state, actions } = useMVC();
  const { isTeacher } = useAuth();

  const lessons = state.data.lessons || [];
  const loading = state.loading.lessons;
  const error = state.errors.lessons;

  useEffect(() => {
    // Load lessons based on user role
    if (isTeacher) {
      actions.lessons.getTodaysLessonsForTeacher().catch(console.error);
    } else {
      actions.lessons.getWeekLessons().catch(console.error);
    }
  }, [actions.lessons, isTeacher]);

  if (error) return <div>Error: {error}</div>;
  if (loading) return <div>Loading lessons...</div>;

  return (
    <div>
      <h2>{isTeacher ? "Today's Lessons" : 'Week Schedule'}</h2>
      {lessons.length === 0 ? (
        <div>No lessons found</div>
      ) : (
        <ul>
          {lessons.map(l => (
            <li key={l.id}>
              {new Date(l.scheduledDate).toLocaleString()} — {l.title} ({l.status})
              <div>Teacher ID: {l.teacherId}</div>
              {l.room && <div>Room: {l.room}</div>}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}


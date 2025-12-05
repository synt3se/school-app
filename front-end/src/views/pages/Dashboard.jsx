import React, { useEffect } from 'react';
import { useAuth } from '../../auth/AuthProvider';
import { useMVC } from '../../controllers/MVCContext';

export default function Dashboard() {
  const { user } = useAuth();
  const { state, actions } = useMVC();

  useEffect(() => {
    // Load upcoming lessons using controller
    actions.lessons.getUpcomingLessons(5).catch(console.error);
  }, [actions.lessons]);

  const upcomingLessons = state.data.lessons || [];

  return (
    <div>
      <h2>Welcome{user ? `, ${user.fullName}` : ''}</h2>

      <section>
        <h3>Upcoming lessons</h3>
        {state.loading.lessons && <div>Loading lessons...</div>}
        {!state.loading.lessons && upcomingLessons.length === 0 && <div>No upcoming lessons</div>}
        <ul>
          {upcomingLessons.map(l => (
            <li key={l.id}>
              {l.title} — {new Date(l.scheduledDate).toLocaleString()} ({l.teacherId})
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}

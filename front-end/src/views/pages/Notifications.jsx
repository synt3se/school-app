import React, { useEffect } from 'react';
import { useMVC } from '../../controllers/MVCContext';

export default function Notifications() {
  const { state, actions } = useMVC();

  const notifications = state.data.notifications || [];
  const loading = state.loading.notifications;
  const error = state.errors.notifications;

  useEffect(() => {
    if (notifications.length === 0 && !loading) {
      actions.notifications.getNotifications().catch(console.error);
    }
  }, [notifications.length, loading, actions.notifications]);

  const markRead = async (id) => {
    try {
      await actions.notifications.markAsRead(id);
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const markAllRead = async () => {
    try {
      await actions.notifications.markAllAsRead();
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  if (error) return <div>Error: {error}</div>;
  if (loading) return <div>Loading notifications...</div>;

  // Sort notifications by priority and date
  const sortedNotifications = actions.notifications.sortByPriority(
    actions.notifications.sortByDate(notifications)
  );

  return (
    <div>
      <h2>Notifications</h2>
      {sortedNotifications.length > 0 && (
        <button onClick={markAllRead}>Mark all read</button>
      )}
      {sortedNotifications.length === 0 ? (
        <div>No notifications found</div>
      ) : (
        <ul>
          {sortedNotifications.map(n => (
            <li key={n.id} style={{ fontWeight: n.isRead ? 'normal' : 'bold' }}>
              <div>
                {n.icon} {n.title} — {n.type}
                {n.priority !== 'normal' && <span> ({n.priority})</span>}
              </div>
              <div>{n.message}</div>
              <div>
                <small>
                  {new Date(n.createdAt).toLocaleString()}
                  {n.expiresAt && ` (Expires: ${new Date(n.expiresAt).toLocaleString()})`}
                </small>
              </div>
              {!n.isRead && (
                <button onClick={() => markRead(n.id)}>Mark read</button>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}


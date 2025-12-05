import { notifications as notificationsApi } from '../api.js';
import BaseService from './BaseService.js';

class NotificationService extends BaseService {
  constructor() {
    super('/api/notifications');
  }

  // Get notifications list with pagination
  async getNotifications(page = 0, size = 20) {
    try {
      const response = await notificationsApi.list(page, size);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get unread notifications count
  async getUnreadCount() {
    try {
      const response = await notificationsApi.unreadCount();
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Mark notification as read
  async markAsRead(id) {
    try {
      const response = await notificationsApi.markRead(id);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Mark all notifications as read
  async markAllAsRead() {
    try {
      const response = await notificationsApi.markAllRead();
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get notification by ID
  async getNotificationById(id) {
    return this.get(id);
  }

  // Create new notification (admin/teacher)
  async createNotification(notificationData) {
    return this.create(notificationData);
  }

  // Delete notification
  async deleteNotification(id) {
    return this.delete(id);
  }
}

export default new NotificationService();

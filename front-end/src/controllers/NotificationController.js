import BaseController from './BaseController.js';
import { Notification } from '../models/index.js';
import { NotificationService } from '../services/index.js';

class NotificationController extends BaseController {
  constructor() {
    super(Notification, NotificationService);
  }

  // Get notifications list with pagination
  async getNotifications(page = 0, size = 20) {
    try {
      const notificationsData = await this.service.getNotifications(page, size);
      const notifications = this.createModels(notificationsData.content || notificationsData);
      this.notify({ type: 'NOTIFICATIONS_LOADED', notifications, pagination: notificationsData });
      return notifications;
    } catch (error) {
      this.handleError(error, ' loading notifications');
    }
  }

  // Get notification by ID
  async getNotificationById(id) {
    try {
      const notificationData = await this.service.getNotificationById(id);
      const notification = this.createModel(notificationData);
      this.notify({ type: 'NOTIFICATION_LOADED', notification });
      return notification;
    } catch (error) {
      this.handleError(error, ` loading notification ${id}`);
    }
  }

  // Get unread notifications count
  async getUnreadCount() {
    try {
      const count = await this.service.getUnreadCount();
      this.notify({ type: 'UNREAD_COUNT_UPDATED', count });
      return count;
    } catch (error) {
      this.handleError(error, ' getting unread count');
    }
  }

  // Mark notification as read
  async markAsRead(id) {
    try {
      await this.service.markAsRead(id);
      this.notify({ type: 'NOTIFICATION_READ', notificationId: id });
      return true;
    } catch (error) {
      this.handleError(error, ` marking notification ${id} as read`);
    }
  }

  // Mark all notifications as read
  async markAllAsRead() {
    try {
      await this.service.markAllAsRead();
      this.notify({ type: 'ALL_NOTIFICATIONS_READ' });
      return true;
    } catch (error) {
      this.handleError(error, ' marking all notifications as read');
    }
  }

  // Create new notification (admin/teacher)
  async createNotification(notificationData) {
    try {
      const createdData = await this.service.createNotification(notificationData);
      const notification = this.createModel(createdData);
      this.notify({ type: 'NOTIFICATION_CREATED', notification });
      return notification;
    } catch (error) {
      this.handleError(error, ' creating notification');
    }
  }

  // Delete notification
  async deleteNotification(id) {
    try {
      await this.service.deleteNotification(id);
      this.notify({ type: 'NOTIFICATION_DELETED', notificationId: id });
      return true;
    } catch (error) {
      this.handleError(error, ` deleting notification ${id}`);
    }
  }

  // Get unread notifications
  getUnreadNotifications(notifications) {
    return notifications.filter(notification => !notification.isRead);
  }

  // Get notifications by type
  getNotificationsByType(notifications, type) {
    return notifications.filter(notification => notification.type === type);
  }

  // Get notifications by priority
  getNotificationsByPriority(notifications, priority) {
    return notifications.filter(notification => notification.priority === priority);
  }

  // Sort notifications by priority
  sortByPriority(notifications) {
    return notifications.sort((a, b) => b.priorityLevel - a.priorityLevel);
  }

  // Sort notifications by date
  sortByDate(notifications, ascending = false) {
    return notifications.sort((a, b) => {
      const dateA = new Date(a.createdAt);
      const dateB = new Date(b.createdAt);
      return ascending ? dateA - dateB : dateB - dateA;
    });
  }

  // Check if notification is expired
  isNotificationExpired(notification) {
    return notification.isExpired();
  }

  // Get active notifications (not expired)
  getActiveNotifications(notifications) {
    return notifications.filter(notification => !notification.isExpired());
  }

  // Validate notification data
  validateNotificationData(notificationData) {
    const notification = new Notification(notificationData);
    try {
      notification.validate();
      return { isValid: true };
    } catch (error) {
      return { isValid: false, error: error.message };
    }
  }
}

export default new NotificationController();

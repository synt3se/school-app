import BaseModel from './BaseModel.js';

class Notification extends BaseModel {
  constructor(data = {}) {
    super(data);
    this.userId = data.userId || null;
    this.title = data.title || '';
    this.message = data.message || '';
    this.type = data.type || 'info'; // info, success, warning, error
    this.isRead = data.isRead || false;
    this.readAt = data.readAt || null;
    this.priority = data.priority || 'normal'; // low, normal, high, urgent
    this.category = data.category || 'general'; // general, lesson, payment, system
    this.actionUrl = data.actionUrl || null;
    this.expiresAt = data.expiresAt || null;
  }

  validate() {
    if (!this.userId) {
      throw new Error('User ID is required');
    }
    if (!this.title.trim()) {
      throw new Error('Title is required');
    }
    if (!this.message.trim()) {
      throw new Error('Message is required');
    }
    return true;
  }

  // Mark as read
  markAsRead() {
    if (!this.isRead) {
      this.isRead = true;
      this.readAt = new Date().toISOString();
      this.update({ isRead: this.isRead, readAt: this.readAt });
    }
  }

  // Mark as unread
  markAsUnread() {
    this.isRead = false;
    this.readAt = null;
    this.update({ isRead: this.isRead, readAt: this.readAt });
  }

  // Check if notification is expired
  isExpired() {
    if (!this.expiresAt) return false;
    return new Date(this.expiresAt) < new Date();
  }

  // Check if notification is urgent
  isUrgent() {
    return this.priority === 'urgent';
  }

  // Check if notification requires action
  requiresAction() {
    return !!this.actionUrl;
  }

  // Get priority level as number
  get priorityLevel() {
    const levels = { low: 1, normal: 2, high: 3, urgent: 4 };
    return levels[this.priority] || 2;
  }

  // Get CSS class based on type
  get typeClass() {
    return `notification-${this.type}`;
  }

  // Get icon based on type
  get icon() {
    const icons = {
      info: 'ℹ️',
      success: '✅',
      warning: '⚠️',
      error: '❌'
    };
    return icons[this.type] || icons.info;
  }
}

export default Notification;

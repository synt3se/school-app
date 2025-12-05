import BaseModel from './BaseModel.js';

class User extends BaseModel {
  constructor(data = {}) {
    super(data);
    this.email = data.email || '';
    this.firstName = data.firstName || '';
    this.lastName = data.lastName || '';
    this.role = data.role || 'student'; // student, teacher, admin
    this.isActive = data.isActive !== undefined ? data.isActive : true;
  }

  get fullName() {
    return `${this.firstName} ${this.lastName}`.trim();
  }

  set fullName(name) {
    const parts = name.split(' ');
    this.firstName = parts[0] || '';
    this.lastName = parts.slice(1).join(' ') || '';
  }

  validate() {
    if (!this.email || !this.email.includes('@')) {
      throw new Error('Valid email is required');
    }
    if (!this.firstName.trim()) {
      throw new Error('First name is required');
    }
    return true;
  }

  // Check if user has specific role
  hasRole(role) {
    return this.role === role;
  }

  // Check if user is teacher or admin
  isTeacher() {
    return ['teacher', 'admin'].includes(this.role);
  }

  // Check if user is admin
  isAdmin() {
    return this.role === 'admin';
  }
}

export default User;

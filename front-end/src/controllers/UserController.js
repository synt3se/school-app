import BaseController from './BaseController.js';
import { User } from '../models/index.js';
import { UserService } from '../services/index.js';

class UserController extends BaseController {
  constructor() {
    super(User, UserService);
  }

  // Get current user profile
  async getCurrentUser() {
    try {
      const userData = await this.service.getCurrentUser();
      const user = this.createModel(userData);
      this.notify({ type: 'USER_LOADED', user });
      return user;
    } catch (error) {
      this.handleError(error, ' loading current user');
    }
  }

  // Update current user profile
  async updateCurrentUser(userData) {
    try {
      const updatedData = await this.service.updateCurrentUser(userData);
      const user = this.createModel(updatedData);
      this.notify({ type: 'USER_UPDATED', user });
      return user;
    } catch (error) {
      this.handleError(error, ' updating current user');
    }
  }

  // Get user by ID (admin/teacher only)
  async getUserById(id) {
    try {
      const userData = await this.service.getUserById(id);
      const user = this.createModel(userData);
      this.notify({ type: 'USER_LOADED', user });
      return user;
    } catch (error) {
      this.handleError(error, ` loading user ${id}`);
    }
  }

  // Update user by ID (admin/teacher only)
  async updateUser(id, userData) {
    try {
      const updatedData = await this.service.updateUser(id, userData);
      const user = this.createModel(updatedData);
      this.notify({ type: 'USER_UPDATED', user });
      return user;
    } catch (error) {
      this.handleError(error, ` updating user ${id}`);
    }
  }

  // Delete user (admin only)
  async deleteUser(id) {
    try {
      await this.service.deleteUser(id);
      this.notify({ type: 'USER_DELETED', userId: id });
      return true;
    } catch (error) {
      this.handleError(error, ` deleting user ${id}`);
    }
  }

  // Validate user data
  validateUserData(userData) {
    const user = new User(userData);
    try {
      user.validate();
      return { isValid: true };
    } catch (error) {
      return { isValid: false, error: error.message };
    }
  }
}

export default new UserController();

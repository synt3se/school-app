import { user as userApi } from '../api.js';
import BaseService from './BaseService.js';

class UserService extends BaseService {
  constructor() {
    super('/api/user');
  }

  // Get current user profile
  async getCurrentUser() {
    try {
      const response = await userApi.getMe();
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Update current user profile
  async updateCurrentUser(userData) {
    try {
      const response = await userApi.update(userData);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get user by ID (if admin/teacher)
  async getUserById(id) {
    return this.get(id);
  }

  // Update user by ID (if admin/teacher)
  async updateUser(id, userData) {
    return this.update(id, userData);
  }

  // Delete user (admin only)
  async deleteUser(id) {
    return this.delete(id);
  }
}

export default new UserService();

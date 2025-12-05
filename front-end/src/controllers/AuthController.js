import BaseController from './BaseController.js';
import { User } from '../models/index.js';
import { AuthService } from '../services/index.js';

class AuthController extends BaseController {
  constructor() {
    super(User, AuthService);
    this.currentUser = null;
    this.isLoading = true;
  }

  // Initialize auth state
  async initialize() {
    try {
      this.isLoading = true;
      this.notify({ type: 'AUTH_LOADING', isLoading: true });

      if (this.service.isAuthenticated()) {
        const userData = await this.service.getCurrentUser();
        this.currentUser = this.createModel(userData);
        this.notify({ type: 'AUTH_SUCCESS', user: this.currentUser });
      } else {
        this.currentUser = null;
        this.notify({ type: 'AUTH_LOGOUT' });
      }
    } catch (error) {
      this.currentUser = null;
      this.service.logout();
      this.notify({ type: 'AUTH_ERROR', error });
    } finally {
      this.isLoading = false;
      this.notify({ type: 'AUTH_LOADING', isLoading: false });
    }
  }

  // Login user
  async login(email, password) {
    try {
      this.notify({ type: 'AUTH_LOADING', isLoading: true });

      const response = await this.service.login(email, password);
      const userData = response.user || await this.service.getCurrentUser();
      this.currentUser = this.createModel(userData);

      this.notify({ type: 'AUTH_SUCCESS', user: this.currentUser });
      return response;
    } catch (error) {
      this.notify({ type: 'AUTH_ERROR', error });
      this.handleError(error, ' during login');
    } finally {
      this.notify({ type: 'AUTH_LOADING', isLoading: false });
    }
  }

  // Register new user
  async register(userData) {
    try {
      this.notify({ type: 'AUTH_LOADING', isLoading: true });

      const response = await this.service.register(userData);
      this.notify({ type: 'REGISTER_SUCCESS', data: response });
      return response;
    } catch (error) {
      this.notify({ type: 'AUTH_ERROR', error });
      this.handleError(error, ' during registration');
    } finally {
      this.notify({ type: 'AUTH_LOADING', isLoading: false });
    }
  }

  // Logout user
  logout() {
    this.service.logout();
    this.currentUser = null;
    this.notify({ type: 'AUTH_LOGOUT' });
  }

  // Get current user
  getCurrentUser() {
    return this.currentUser;
  }

  // Check if user is authenticated
  isAuthenticated() {
    return !!this.currentUser;
  }

  // Check if user has specific role
  hasRole(role) {
    return this.currentUser && this.currentUser.hasRole(role);
  }

  // Check if user is teacher
  isTeacher() {
    return this.currentUser && this.currentUser.isTeacher();
  }

  // Check if user is admin
  isAdmin() {
    return this.currentUser && this.currentUser.isAdmin();
  }

  // Refresh current user data
  async refreshUser() {
    try {
      const userData = await this.service.getCurrentUser();
      this.currentUser = this.createModel(userData);
      this.notify({ type: 'USER_UPDATED', user: this.currentUser });
      return this.currentUser;
    } catch (error) {
      this.handleError(error, ' during user refresh');
    }
  }
}

export default new AuthController();

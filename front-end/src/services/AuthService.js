import { auth, user as userApi } from '../api.js';
import { TOKEN } from '../api.js';

class AuthService {
  // Login user
  async login(email, password) {
    try {
      const response = await auth.login(email, password);
      if (response.token) {
        TOKEN.set(response.token);
      }
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Register new user
  async register(userData) {
    try {
      const response = await auth.register(userData);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Logout user
  logout() {
    TOKEN.remove();
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

  // Check if user is authenticated
  isAuthenticated() {
    return !!TOKEN.get();
  }

  // Get stored token
  getToken() {
    return TOKEN.get();
  }

  // Handle authentication errors
  handleError(error) {
    console.error('AuthService error:', error);

    // Clear token on 401
    if (error.response && error.response.status === 401) {
      this.logout();
    }

    throw error;
  }
}

export default new AuthService();

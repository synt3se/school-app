import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { AuthController } from '../controllers/index.js';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Handle auth controller updates
  const handleAuthUpdate = useCallback((update) => {
    switch (update.type) {
      case 'AUTH_LOADING':
        setLoading(update.isLoading);
        break;
      case 'AUTH_SUCCESS':
        setUser(update.user);
        setLoading(false);
        break;
      case 'AUTH_LOGOUT':
        setUser(null);
        setLoading(false);
        break;
      case 'AUTH_ERROR':
        setUser(null);
        setLoading(false);
        break;
      case 'USER_UPDATED':
        setUser(update.user);
        break;
      default:
        break;
    }
  }, []);

  // Initialize auth state
  useEffect(() => {
    const unsubscribe = AuthController.subscribe(handleAuthUpdate);
    AuthController.initialize();

    return unsubscribe;
  }, [handleAuthUpdate]);

  // Auth methods using controller
  const login = async (email, password) => {
    return await AuthController.login(email, password);
  };

  const register = async (payload) => {
    return await AuthController.register(payload);
  };

  const logout = () => {
    AuthController.logout();
  };

  const refreshUser = async () => {
    return await AuthController.refreshUser();
  };

  // Computed auth state
  const isAuthenticated = AuthController.isAuthenticated();
  const hasRole = (role) => AuthController.hasRole(role);
  const isTeacher = AuthController.isTeacher();
  const isAdmin = AuthController.isAdmin();

  return (
    <AuthContext.Provider value={{
      user,
      loading,
      login,
      logout,
      register,
      refreshUser,
      isAuthenticated,
      hasRole,
      isTeacher,
      isAdmin
    }}>
      {children}
    </AuthContext.Provider>
  );
}


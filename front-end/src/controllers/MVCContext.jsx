import React, { createContext, useContext, useEffect, useReducer, useCallback } from 'react';
import {
  AuthController,
  UserController,
  ChildController,
  LessonController,
  PaymentController,
  NotificationController
} from './index.js';

const MVCContext = createContext(null);

export const useMVC = () => useContext(MVCContext);

// MVC State reducer
const mvcReducer = (state, action) => {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: { ...state.loading, [action.key]: action.loading } };

    case 'SET_DATA':
      return { ...state, data: { ...state.data, [action.key]: action.data } };

    case 'UPDATE_DATA':
      return {
        ...state,
        data: {
          ...state.data,
          [action.key]: Array.isArray(state.data[action.key])
            ? state.data[action.key].map(item =>
                item.id === action.data.id ? action.data : item
              )
            : action.data
        }
      };

    case 'ADD_DATA':
      return {
        ...state,
        data: {
          ...state.data,
          [action.key]: Array.isArray(state.data[action.key])
            ? [...state.data[action.key], action.data]
            : action.data
        }
      };

    case 'REMOVE_DATA':
      return {
        ...state,
        data: {
          ...state.data,
          [action.key]: Array.isArray(state.data[action.key])
            ? state.data[action.key].filter(item => item.id !== action.id)
            : null
        }
      };

    case 'SET_ERROR':
      return { ...state, errors: { ...state.errors, [action.key]: action.error } };

    case 'CLEAR_ERROR':
      return { ...state, errors: { ...state.errors, [action.key]: null } };

    default:
      return state;
  }
};

// Initial state
const initialState = {
  loading: {
    auth: true,
    user: false,
    child: false,
    lessons: false,
    payments: false,
    notifications: false
  },
  data: {
    user: null,
    child: null,
    lessons: [],
    payments: [],
    notifications: []
  },
  errors: {
    auth: null,
    user: null,
    child: null,
    lessons: null,
    payments: null,
    notifications: null
  }
};

export function MVCProvider({ children }) {
  const [state, dispatch] = useReducer(mvcReducer, initialState);

  // Global update handler
  const handleGlobalUpdate = useCallback((update) => {
    switch (update.type) {
      // Auth updates
      case 'AUTH_LOADING':
        dispatch({ type: 'SET_LOADING', key: 'auth', loading: update.isLoading });
        break;
      case 'AUTH_SUCCESS':
        dispatch({ type: 'SET_DATA', key: 'user', data: update.user });
        dispatch({ type: 'CLEAR_ERROR', key: 'auth' });
        break;
      case 'AUTH_LOGOUT':
        dispatch({ type: 'SET_DATA', key: 'user', data: null });
        dispatch({ type: 'SET_DATA', key: 'child', data: null });
        break;
      case 'AUTH_ERROR':
        dispatch({ type: 'SET_ERROR', key: 'auth', error: update.error });
        break;

      // User updates
      case 'USER_LOADED':
      case 'USER_UPDATED':
        dispatch({ type: 'SET_DATA', key: 'user', data: update.user });
        dispatch({ type: 'SET_LOADING', key: 'user', loading: false });
        break;
      case 'USER_DELETED':
        // Handle user deletion if needed
        break;

      // Child updates
      case 'CHILD_LOADED':
      case 'CHILD_UPDATED':
        dispatch({ type: 'SET_DATA', key: 'child', data: update.child });
        dispatch({ type: 'SET_LOADING', key: 'child', loading: false });
        break;

      // Lesson updates
      case 'UPCOMING_LESSONS_LOADED':
      case 'WEEK_LESSONS_LOADED':
      case 'TODAY_LESSONS_LOADED':
        dispatch({ type: 'SET_DATA', key: 'lessons', data: update.lessons });
        dispatch({ type: 'SET_LOADING', key: 'lessons', loading: false });
        break;
      case 'LESSON_CREATED':
        dispatch({ type: 'ADD_DATA', key: 'lessons', data: update.lesson });
        break;
      case 'LESSON_UPDATED':
        dispatch({ type: 'UPDATE_DATA', key: 'lessons', data: update.lesson });
        break;
      case 'LESSON_DELETED':
        dispatch({ type: 'REMOVE_DATA', key: 'lessons', id: update.lessonId });
        break;

      // Payment updates
      case 'PAYMENTS_LOADED':
        dispatch({ type: 'SET_DATA', key: 'payments', data: update.payments });
        dispatch({ type: 'SET_LOADING', key: 'payments', loading: false });
        break;
      case 'PAYMENT_CREATED':
        dispatch({ type: 'ADD_DATA', key: 'payments', data: update.payment });
        break;
      case 'PAYMENT_UPDATED':
        dispatch({ type: 'UPDATE_DATA', key: 'payments', data: update.payment });
        break;

      // Notification updates
      case 'NOTIFICATIONS_LOADED':
        dispatch({ type: 'SET_DATA', key: 'notifications', data: update.notifications });
        dispatch({ type: 'SET_LOADING', key: 'notifications', loading: false });
        break;
      case 'NOTIFICATION_CREATED':
        dispatch({ type: 'ADD_DATA', key: 'notifications', data: update.notification });
        break;
      case 'NOTIFICATION_READ':
        // Update notification read status
        const notifications = state.data.notifications.map(n =>
          n.id === update.notificationId ? { ...n, isRead: true, readAt: new Date().toISOString() } : n
        );
        dispatch({ type: 'SET_DATA', key: 'notifications', data: notifications });
        break;
      case 'NOTIFICATION_DELETED':
        dispatch({ type: 'REMOVE_DATA', key: 'notifications', id: update.notificationId });
        break;

      default:
        break;
    }
  }, [state.data.notifications]);

  // Subscribe to all controllers
  useEffect(() => {
    const unsubscribers = [
      AuthController.subscribe(handleGlobalUpdate),
      UserController.subscribe(handleGlobalUpdate),
      ChildController.subscribe(handleGlobalUpdate),
      LessonController.subscribe(handleGlobalUpdate),
      PaymentController.subscribe(handleGlobalUpdate),
      NotificationController.subscribe(handleGlobalUpdate)
    ];

    return () => {
      unsubscribers.forEach(unsubscribe => unsubscribe());
    };
  }, [handleGlobalUpdate]);

  // Controller actions
  const actions = {
    // Auth
    auth: {
      login: AuthController.login.bind(AuthController),
      register: AuthController.register.bind(AuthController),
      logout: AuthController.logout.bind(AuthController),
      refreshUser: AuthController.refreshUser.bind(AuthController),
      isAuthenticated: () => AuthController.isAuthenticated(),
      hasRole: AuthController.hasRole.bind(AuthController),
      isTeacher: () => AuthController.isTeacher(),
      isAdmin: () => AuthController.isAdmin()
    },

    // User
    user: {
      getCurrentUser: UserController.getCurrentUser.bind(UserController),
      updateCurrentUser: UserController.updateCurrentUser.bind(UserController),
      validateUserData: UserController.validateUserData.bind(UserController)
    },

    // Child
    child: {
      getCurrentChild: ChildController.getCurrentChild.bind(ChildController),
      updateCurrentChild: ChildController.updateCurrentChild.bind(ChildController),
      getChildJournal: ChildController.getChildJournal.bind(ChildController),
      validateChildData: ChildController.validateChildData.bind(ChildController),
      calculateAge: ChildController.calculateAge.bind(ChildController)
    },

    // Lessons
    lessons: {
      getUpcomingLessons: LessonController.getUpcomingLessons.bind(LessonController),
      getWeekLessons: LessonController.getWeekLessons.bind(LessonController),
      getAvailableLessons: LessonController.getAvailableLessons.bind(LessonController),
      getTodaysLessonsForTeacher: LessonController.getTodaysLessonsForTeacher.bind(LessonController),
      getLessonById: LessonController.getLessonById.bind(LessonController),
      createLesson: LessonController.createLesson.bind(LessonController),
      updateLesson: LessonController.updateLesson.bind(LessonController),
      cancelLesson: LessonController.cancelLesson.bind(LessonController),
      rescheduleLesson: LessonController.rescheduleLesson.bind(LessonController),
      restoreLesson: LessonController.restoreLesson.bind(LessonController),
      markAttendance: LessonController.markAttendance.bind(LessonController),
      deleteLesson: LessonController.deleteLesson.bind(LessonController),
      filterLessonsByStatus: LessonController.filterLessonsByStatus.bind(LessonController),
      filterLessonsByDateRange: LessonController.filterLessonsByDateRange.bind(LessonController),
      sortLessonsByDate: LessonController.sortLessonsByDate.bind(LessonController)
    },

    // Payments
    payments: {
      getPayments: PaymentController.getPayments.bind(PaymentController),
      getPaymentById: PaymentController.getPaymentById.bind(PaymentController),
      createPayment: PaymentController.createPayment.bind(PaymentController),
      updatePayment: PaymentController.updatePayment.bind(PaymentController),
      processPayment: PaymentController.processPayment.bind(PaymentController),
      getPaymentPrices: PaymentController.getPaymentPrices.bind(PaymentController),
      calculateTotal: PaymentController.calculateTotal.bind(PaymentController),
      isPaymentOverdue: PaymentController.isPaymentOverdue.bind(PaymentController),
      formatAmount: PaymentController.formatAmount.bind(PaymentController),
      validatePaymentData: PaymentController.validatePaymentData.bind(PaymentController)
    },

    // Notifications
    notifications: {
      getNotifications: NotificationController.getNotifications.bind(NotificationController),
      getNotificationById: NotificationController.getNotificationById.bind(NotificationController),
      getUnreadCount: NotificationController.getUnreadCount.bind(NotificationController),
      markAsRead: NotificationController.markAsRead.bind(NotificationController),
      markAllAsRead: NotificationController.markAllAsRead.bind(NotificationController),
      createNotification: NotificationController.createNotification.bind(NotificationController),
      deleteNotification: NotificationController.deleteNotification.bind(NotificationController),
      getUnreadNotifications: NotificationController.getUnreadNotifications.bind(NotificationController),
      sortByPriority: NotificationController.sortByPriority.bind(NotificationController),
      sortByDate: NotificationController.sortByDate.bind(NotificationController),
      validateNotificationData: NotificationController.validateNotificationData.bind(NotificationController)
    }
  };

  const contextValue = {
    state,
    actions,
    dispatch
  };

  return (
    <MVCContext.Provider value={contextValue}>
      {children}
    </MVCContext.Provider>
  );
}

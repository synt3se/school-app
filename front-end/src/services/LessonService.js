import { lessons as lessonsApi } from '../api.js';
import BaseService from './BaseService.js';

class LessonService extends BaseService {
  constructor() {
    super('/api/lessons');
  }

  // Get upcoming lessons
  async getUpcomingLessons(limit = 3) {
    try {
      const response = await lessonsApi.upcoming(limit);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get lessons for current week
  async getWeekLessons() {
    try {
      const response = await lessonsApi.week();
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get available lessons for a course
  async getAvailableLessons(courseId) {
    try {
      const response = await lessonsApi.available(courseId);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Cancel lesson
  async cancelLesson(id) {
    try {
      const response = await lessonsApi.cancel(id);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Reschedule lesson
  async rescheduleLesson(fromLessonId, toLessonId) {
    try {
      const response = await lessonsApi.reschedule(fromLessonId, toLessonId);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Restore missed lesson
  async restoreLesson(missedLessonId, targetLessonId) {
    try {
      const response = await lessonsApi.restore(missedLessonId, targetLessonId);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get today's lessons for teacher
  async getTodaysLessonsForTeacher() {
    try {
      const response = await lessonsApi.todayForTeacher();
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Mark attendance for lesson
  async markAttendance(id, attendanceData) {
    try {
      const response = await lessonsApi.markAttendance(id, attendanceData);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get lesson by ID
  async getLessonById(id) {
    return this.get(id);
  }

  // Update lesson
  async updateLesson(id, lessonData) {
    return this.update(id, lessonData);
  }

  // Create new lesson
  async createLesson(lessonData) {
    return this.create(lessonData);
  }

  // Delete lesson
  async deleteLesson(id) {
    return this.delete(id);
  }
}

export default new LessonService();

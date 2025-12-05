import BaseController from './BaseController.js';
import { Lesson } from '../models/index.js';
import { LessonService } from '../services/index.js';

class LessonController extends BaseController {
  constructor() {
    super(Lesson, LessonService);
  }

  // Get upcoming lessons
  async getUpcomingLessons(limit = 3) {
    try {
      const lessonsData = await this.service.getUpcomingLessons(limit);
      const lessons = this.createModels(lessonsData);
      this.notify({ type: 'UPCOMING_LESSONS_LOADED', lessons });
      return lessons;
    } catch (error) {
      this.handleError(error, ' loading upcoming lessons');
    }
  }

  // Get lessons for current week
  async getWeekLessons() {
    try {
      const lessonsData = await this.service.getWeekLessons();
      const lessons = this.createModels(lessonsData);
      this.notify({ type: 'WEEK_LESSONS_LOADED', lessons });
      return lessons;
    } catch (error) {
      this.handleError(error, ' loading week lessons');
    }
  }

  // Get available lessons for a course
  async getAvailableLessons(courseId) {
    try {
      const lessonsData = await this.service.getAvailableLessons(courseId);
      const lessons = this.createModels(lessonsData);
      this.notify({ type: 'AVAILABLE_LESSONS_LOADED', lessons, courseId });
      return lessons;
    } catch (error) {
      this.handleError(error, ` loading available lessons for course ${courseId}`);
    }
  }

  // Get today's lessons for teacher
  async getTodaysLessonsForTeacher() {
    try {
      const lessonsData = await this.service.getTodaysLessonsForTeacher();
      const lessons = this.createModels(lessonsData);
      this.notify({ type: 'TODAY_LESSONS_LOADED', lessons });
      return lessons;
    } catch (error) {
      this.handleError(error, ' loading today\'s lessons');
    }
  }

  // Get lesson by ID
  async getLessonById(id) {
    try {
      const lessonData = await this.service.getLessonById(id);
      const lesson = this.createModel(lessonData);
      this.notify({ type: 'LESSON_LOADED', lesson });
      return lesson;
    } catch (error) {
      this.handleError(error, ` loading lesson ${id}`);
    }
  }

  // Create new lesson
  async createLesson(lessonData) {
    try {
      const createdData = await this.service.createLesson(lessonData);
      const lesson = this.createModel(createdData);
      this.notify({ type: 'LESSON_CREATED', lesson });
      return lesson;
    } catch (error) {
      this.handleError(error, ' creating lesson');
    }
  }

  // Update lesson
  async updateLesson(id, lessonData) {
    try {
      const updatedData = await this.service.updateLesson(id, lessonData);
      const lesson = this.createModel(updatedData);
      this.notify({ type: 'LESSON_UPDATED', lesson });
      return lesson;
    } catch (error) {
      this.handleError(error, ` updating lesson ${id}`);
    }
  }

  // Cancel lesson
  async cancelLesson(id) {
    try {
      await this.service.cancelLesson(id);
      this.notify({ type: 'LESSON_CANCELLED', lessonId: id });
      return true;
    } catch (error) {
      this.handleError(error, ` cancelling lesson ${id}`);
    }
  }

  // Reschedule lesson
  async rescheduleLesson(fromLessonId, toLessonId) {
    try {
      await this.service.rescheduleLesson(fromLessonId, toLessonId);
      this.notify({ type: 'LESSON_RESCHEDULED', fromLessonId, toLessonId });
      return true;
    } catch (error) {
      this.handleError(error, ` rescheduling lesson ${fromLessonId} to ${toLessonId}`);
    }
  }

  // Restore missed lesson
  async restoreLesson(missedLessonId, targetLessonId) {
    try {
      await this.service.restoreLesson(missedLessonId, targetLessonId);
      this.notify({ type: 'LESSON_RESTORED', missedLessonId, targetLessonId });
      return true;
    } catch (error) {
      this.handleError(error, ` restoring lesson ${missedLessonId} to ${targetLessonId}`);
    }
  }

  // Mark attendance for lesson
  async markAttendance(id, attendanceData) {
    try {
      await this.service.markAttendance(id, attendanceData);
      this.notify({ type: 'ATTENDANCE_MARKED', lessonId: id, attendance: attendanceData });
      return true;
    } catch (error) {
      this.handleError(error, ` marking attendance for lesson ${id}`);
    }
  }

  // Delete lesson
  async deleteLesson(id) {
    try {
      await this.service.deleteLesson(id);
      this.notify({ type: 'LESSON_DELETED', lessonId: id });
      return true;
    } catch (error) {
      this.handleError(error, ` deleting lesson ${id}`);
    }
  }

  // Filter lessons by status
  filterLessonsByStatus(lessons, status) {
    return lessons.filter(lesson => lesson.status === status);
  }

  // Filter lessons by date range
  filterLessonsByDateRange(lessons, startDate, endDate) {
    const start = new Date(startDate);
    const end = new Date(endDate);
    return lessons.filter(lesson => {
      const lessonDate = new Date(lesson.scheduledDate);
      return lessonDate >= start && lessonDate <= end;
    });
  }

  // Sort lessons by date
  sortLessonsByDate(lessons, ascending = true) {
    return lessons.sort((a, b) => {
      const dateA = new Date(a.scheduledDate);
      const dateB = new Date(b.scheduledDate);
      return ascending ? dateA - dateB : dateB - dateA;
    });
  }
}

export default new LessonController();

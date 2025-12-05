import BaseModel from './BaseModel.js';

class Lesson extends BaseModel {
  constructor(data = {}) {
    super(data);
    this.title = data.title || '';
    this.description = data.description || '';
    this.courseId = data.courseId || null;
    this.teacherId = data.teacherId || null;
    this.scheduledDate = data.scheduledDate || null;
    this.duration = data.duration || 60; // minutes
    this.status = data.status || 'scheduled'; // scheduled, completed, cancelled
    this.attendance = data.attendance || {}; // childId -> status
    this.room = data.room || '';
    this.maxCapacity = data.maxCapacity || 20;
  }

  validate() {
    if (!this.title.trim()) {
      throw new Error('Title is required');
    }
    if (!this.courseId) {
      throw new Error('Course ID is required');
    }
    if (!this.teacherId) {
      throw new Error('Teacher ID is required');
    }
    if (!this.scheduledDate) {
      throw new Error('Scheduled date is required');
    }
    return true;
  }

  // Check if lesson is in the past
  isPast() {
    return new Date(this.scheduledDate) < new Date();
  }

  // Check if lesson is today
  isToday() {
    const today = new Date();
    const lessonDate = new Date(this.scheduledDate);
    return lessonDate.toDateString() === today.toDateString();
  }

  // Check if lesson is upcoming
  isUpcoming() {
    return new Date(this.scheduledDate) > new Date();
  }

  // Get end time
  get endTime() {
    if (!this.scheduledDate) return null;
    const start = new Date(this.scheduledDate);
    const end = new Date(start.getTime() + this.duration * 60000);
    return end.toISOString();
  }

  // Mark attendance for a child
  markAttendance(childId, status) {
    this.attendance[childId] = status;
    this.update({ attendance: this.attendance });
  }

  // Get attendance status for a child
  getAttendanceStatus(childId) {
    return this.attendance[childId] || 'absent';
  }

  // Get attendance count
  get attendanceCount() {
    return Object.keys(this.attendance).length;
  }

  // Check if lesson is at capacity
  isAtCapacity() {
    return this.attendanceCount >= this.maxCapacity;
  }

  // Check if child is registered
  isChildRegistered(childId) {
    return childId in this.attendance;
  }

  // Cancel lesson
  cancel() {
    this.status = 'cancelled';
    this.update({ status: this.status });
  }

  // Complete lesson
  complete() {
    this.status = 'completed';
    this.update({ status: this.status });
  }
}

export default Lesson;

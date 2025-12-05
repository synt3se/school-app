import BaseModel from './BaseModel.js';

class Child extends BaseModel {
  constructor(data = {}) {
    super(data);
    this.firstName = data.firstName || '';
    this.lastName = data.lastName || '';
    this.dateOfBirth = data.dateOfBirth || null;
    this.grade = data.grade || '';
    this.parentId = data.parentId || null;
    this.enrolledCourses = data.enrolledCourses || [];
    this.attendance = data.attendance || {};
  }

  get fullName() {
    return `${this.firstName} ${this.lastName}`.trim();
  }

  set fullName(name) {
    const parts = name.split(' ');
    this.firstName = parts[0] || '';
    this.lastName = parts.slice(1).join(' ') || '';
  }

  get age() {
    if (!this.dateOfBirth) return null;
    const today = new Date();
    const birthDate = new Date(this.dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    return age;
  }

  validate() {
    if (!this.firstName.trim()) {
      throw new Error('First name is required');
    }
    if (!this.lastName.trim()) {
      throw new Error('Last name is required');
    }
    if (!this.dateOfBirth) {
      throw new Error('Date of birth is required');
    }
    return true;
  }

  // Add course enrollment
  enrollInCourse(courseId) {
    if (!this.enrolledCourses.includes(courseId)) {
      this.enrolledCourses.push(courseId);
      this.update({ enrolledCourses: this.enrolledCourses });
    }
  }

  // Remove course enrollment
  unenrollFromCourse(courseId) {
    this.enrolledCourses = this.enrolledCourses.filter(id => id !== courseId);
    this.update({ enrolledCourses: this.enrolledCourses });
  }

  // Check if enrolled in course
  isEnrolledInCourse(courseId) {
    return this.enrolledCourses.includes(courseId);
  }

  // Get attendance for a specific lesson
  getAttendanceForLesson(lessonId) {
    return this.attendance[lessonId] || null;
  }

  // Set attendance for a lesson
  setAttendanceForLesson(lessonId, status) {
    this.attendance[lessonId] = status;
    this.update({ attendance: this.attendance });
  }
}

export default Child;

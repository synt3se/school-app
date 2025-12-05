import BaseController from './BaseController.js';
import { Child } from '../models/index.js';
import { ChildService } from '../services/index.js';

class ChildController extends BaseController {
  constructor() {
    super(Child, ChildService);
  }

  // Get current user's child
  async getCurrentChild() {
    try {
      const childData = await this.service.getCurrentChild();
      const child = this.createModel(childData);
      this.notify({ type: 'CHILD_LOADED', child });
      return child;
    } catch (error) {
      this.handleError(error, ' loading current child');
    }
  }

  // Update current user's child
  async updateCurrentChild(childData) {
    try {
      const updatedData = await this.service.updateCurrentChild(childData);
      const child = this.createModel(updatedData);
      this.notify({ type: 'CHILD_UPDATED', child });
      return child;
    } catch (error) {
      this.handleError(error, ' updating current child');
    }
  }

  // Get child's journal/records
  async getChildJournal() {
    try {
      const journalData = await this.service.getChildJournal();
      this.notify({ type: 'CHILD_JOURNAL_LOADED', journal: journalData });
      return journalData;
    } catch (error) {
      this.handleError(error, ' loading child journal');
    }
  }

  // Get child by ID (teacher/admin)
  async getChildById(id) {
    try {
      const childData = await this.service.getChildById(id);
      const child = this.createModel(childData);
      this.notify({ type: 'CHILD_LOADED', child });
      return child;
    } catch (error) {
      this.handleError(error, ` loading child ${id}`);
    }
  }

  // Update child by ID (teacher/admin)
  async updateChild(id, childData) {
    try {
      const updatedData = await this.service.updateChild(id, childData);
      const child = this.createModel(updatedData);
      this.notify({ type: 'CHILD_UPDATED', child });
      return child;
    } catch (error) {
      this.handleError(error, ` updating child ${id}`);
    }
  }

  // Validate child data
  validateChildData(childData) {
    const child = new Child(childData);
    try {
      child.validate();
      return { isValid: true };
    } catch (error) {
      return { isValid: false, error: error.message };
    }
  }

  // Calculate child's age
  calculateAge(child) {
    return child.age;
  }

  // Check if child can be enrolled in course
  canEnrollInCourse(child, courseId) {
    return !child.isEnrolledInCourse(courseId);
  }

  // Enroll child in course
  enrollInCourse(child, courseId) {
    child.enrollInCourse(courseId);
    this.notify({ type: 'CHILD_ENROLLED', child, courseId });
  }

  // Unenroll child from course
  unenrollFromCourse(child, courseId) {
    child.unenrollFromCourse(courseId);
    this.notify({ type: 'CHILD_UNENROLLED', child, courseId });
  }
}

export default new ChildController();

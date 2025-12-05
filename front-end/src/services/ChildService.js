import { child as childApi } from '../api.js';
import BaseService from './BaseService.js';

class ChildService extends BaseService {
  constructor() {
    super('/api/child');
  }

  // Get current user's child
  async getCurrentChild() {
    try {
      const response = await childApi.get();
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Update current user's child
  async updateCurrentChild(childData) {
    try {
      const response = await childApi.update(childData);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get child's journal/records
  async getChildJournal() {
    try {
      const response = await childApi.journal();
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get child by ID (teacher/admin)
  async getChildById(id) {
    return this.get(id);
  }

  // Update child by ID (teacher/admin)
  async updateChild(id, childData) {
    return this.update(id, childData);
  }
}

export default new ChildService();

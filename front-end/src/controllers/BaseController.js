import { BaseModel } from '../models/index.js';

class BaseController {
  constructor(ModelClass, service) {
    this.ModelClass = ModelClass;
    this.service = service;
    this.listeners = new Set();
  }

  // Subscribe to controller updates
  subscribe(listener) {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  // Notify all listeners of updates
  notify(data) {
    this.listeners.forEach(listener => listener(data));
  }

  // Convert API data to model instances
  createModel(data) {
    return new this.ModelClass(data);
  }

  // Convert array of API data to model instances
  createModels(dataArray) {
    return dataArray.map(data => this.createModel(data));
  }

  // Handle controller errors
  handleError(error, context = '') {
    console.error(`Error in ${this.constructor.name}${context}:`, error);
    throw error;
  }
}

export default BaseController;

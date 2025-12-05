import api from '../api.js';

class BaseService {
  constructor(endpoint) {
    this.endpoint = endpoint;
  }

  // Generic GET request
  async get(id = null) {
    const url = id ? `/${id}` : '';
    const response = await api.get(`${this.endpoint}${url}`);
    return response.data;
  }

  // Generic GET list with pagination
  async getList(params = {}) {
    const response = await api.get(this.endpoint, { params });
    return response.data;
  }

  // Generic POST request
  async create(data) {
    const response = await api.post(this.endpoint, data);
    return response.data;
  }

  // Generic PUT request
  async update(id, data) {
    const response = await api.put(`${this.endpoint}/${id}`, data);
    return response.data;
  }

  // Generic DELETE request
  async delete(id) {
    const response = await api.delete(`${this.endpoint}/${id}`);
    return response.data;
  }

  // Handle API errors
  handleError(error) {
    console.error(`Error in ${this.constructor.name}:`, error);
    throw error;
  }
}

export default BaseService;

class BaseModel {
  constructor(data = {}) {
    this.id = data.id || null;
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
    Object.assign(this, data);
  }

  // Convert model to plain object for API calls
  toJSON() {
    const { id, createdAt, updatedAt, ...data } = this;
    return data;
  }

  // Update model with new data
  update(data) {
    Object.assign(this, data);
    this.updatedAt = new Date().toISOString();
    return this;
  }

  // Check if model is new (not saved to backend yet)
  isNew() {
    return !this.id;
  }

  // Validate model data
  validate() {
    return true; // Override in subclasses
  }
}

export default BaseModel;

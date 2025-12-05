import BaseModel from './BaseModel.js';

class Payment extends BaseModel {
  constructor(data = {}) {
    super(data);
    this.userId = data.userId || null;
    this.amount = data.amount || 0;
    this.currency = data.currency || 'USD';
    this.status = data.status || 'pending'; // pending, completed, failed, refunded
    this.paymentMethod = data.paymentMethod || 'card';
    this.description = data.description || '';
    this.transactionId = data.transactionId || null;
    this.invoiceNumber = data.invoiceNumber || null;
    this.dueDate = data.dueDate || null;
    this.paidAt = data.paidAt || null;
    this.items = data.items || []; // Array of payment items
  }

  validate() {
    if (!this.userId) {
      throw new Error('User ID is required');
    }
    if (this.amount <= 0) {
      throw new Error('Amount must be greater than 0');
    }
    return true;
  }

  // Check if payment is completed
  isCompleted() {
    return this.status === 'completed';
  }

  // Check if payment is pending
  isPending() {
    return this.status === 'pending';
  }

  // Check if payment is overdue
  isOverdue() {
    if (!this.dueDate || this.isCompleted()) return false;
    return new Date(this.dueDate) < new Date();
  }

  // Mark payment as completed
  complete(transactionId = null) {
    this.status = 'completed';
    this.paidAt = new Date().toISOString();
    if (transactionId) {
      this.transactionId = transactionId;
    }
    this.update({
      status: this.status,
      paidAt: this.paidAt,
      transactionId: this.transactionId
    });
  }

  // Mark payment as failed
  fail() {
    this.status = 'failed';
    this.update({ status: this.status });
  }

  // Refund payment
  refund() {
    this.status = 'refunded';
    this.update({ status: this.status });
  }

  // Get formatted amount
  get formattedAmount() {
    return `${this.currency} ${this.amount.toFixed(2)}`;
  }

  // Add payment item
  addItem(item) {
    this.items.push(item);
    this.recalculateAmount();
  }

  // Remove payment item
  removeItem(itemId) {
    this.items = this.items.filter(item => item.id !== itemId);
    this.recalculateAmount();
  }

  // Recalculate total amount from items
  recalculateAmount() {
    this.amount = this.items.reduce((total, item) => total + item.amount, 0);
    this.update({ amount: this.amount, items: this.items });
  }
}

export default Payment;

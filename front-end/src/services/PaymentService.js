import { payments as paymentsApi } from '../api.js';
import BaseService from './BaseService.js';

class PaymentService extends BaseService {
  constructor() {
    super('/api/payments');
  }

  // Get payments list with pagination
  async getPayments(page = 0, size = 20) {
    try {
      const response = await paymentsApi.list(page, size);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Create new payment
  async createPayment(paymentData) {
    try {
      const response = await paymentsApi.create(paymentData);
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get payment prices/configuration
  async getPaymentPrices() {
    try {
      const response = await paymentsApi.prices();
      return response;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Get payment by ID
  async getPaymentById(id) {
    return this.get(id);
  }

  // Update payment
  async updatePayment(id, paymentData) {
    return this.update(id, paymentData);
  }

  // Process payment
  async processPayment(id, paymentDetails) {
    // This would typically call a payment processor
    // For now, just update the payment status
    return this.updatePayment(id, { status: 'completed', ...paymentDetails });
  }
}

export default new PaymentService();

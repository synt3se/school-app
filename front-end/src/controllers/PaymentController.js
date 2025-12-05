import BaseController from './BaseController.js';
import { Payment } from '../models/index.js';
import { PaymentService } from '../services/index.js';

class PaymentController extends BaseController {
  constructor() {
    super(Payment, PaymentService);
  }

  // Get payments list with pagination
  async getPayments(page = 0, size = 20) {
    try {
      const paymentsData = await this.service.getPayments(page, size);
      const payments = this.createModels(paymentsData.content || paymentsData);
      this.notify({ type: 'PAYMENTS_LOADED', payments, pagination: paymentsData });
      return payments;
    } catch (error) {
      this.handleError(error, ' loading payments');
    }
  }

  // Get payment by ID
  async getPaymentById(id) {
    try {
      const paymentData = await this.service.getPaymentById(id);
      const payment = this.createModel(paymentData);
      this.notify({ type: 'PAYMENT_LOADED', payment });
      return payment;
    } catch (error) {
      this.handleError(error, ` loading payment ${id}`);
    }
  }

  // Create new payment
  async createPayment(paymentData) {
    try {
      const createdData = await this.service.createPayment(paymentData);
      const payment = this.createModel(createdData);
      this.notify({ type: 'PAYMENT_CREATED', payment });
      return payment;
    } catch (error) {
      this.handleError(error, ' creating payment');
    }
  }

  // Update payment
  async updatePayment(id, paymentData) {
    try {
      const updatedData = await this.service.updatePayment(id, paymentData);
      const payment = this.createModel(updatedData);
      this.notify({ type: 'PAYMENT_UPDATED', payment });
      return payment;
    } catch (error) {
      this.handleError(error, ` updating payment ${id}`);
    }
  }

  // Process payment
  async processPayment(id, paymentDetails) {
    try {
      const result = await this.service.processPayment(id, paymentDetails);
      const payment = this.createModel(result);
      this.notify({ type: 'PAYMENT_PROCESSED', payment });
      return payment;
    } catch (error) {
      this.handleError(error, ` processing payment ${id}`);
    }
  }

  // Get payment prices/configuration
  async getPaymentPrices() {
    try {
      const prices = await this.service.getPaymentPrices();
      this.notify({ type: 'PAYMENT_PRICES_LOADED', prices });
      return prices;
    } catch (error) {
      this.handleError(error, ' loading payment prices');
    }
  }

  // Calculate total amount for items
  calculateTotal(items) {
    return items.reduce((total, item) => total + item.amount, 0);
  }

  // Check if payment is overdue
  isPaymentOverdue(payment) {
    return payment.isOverdue();
  }

  // Get overdue payments
  getOverduePayments(payments) {
    return payments.filter(payment => payment.isOverdue());
  }

  // Get pending payments
  getPendingPayments(payments) {
    return payments.filter(payment => payment.isPending());
  }

  // Get completed payments
  getCompletedPayments(payments) {
    return payments.filter(payment => payment.isCompleted());
  }

  // Format payment amount
  formatAmount(payment) {
    return payment.formattedAmount;
  }

  // Validate payment data
  validatePaymentData(paymentData) {
    const payment = new Payment(paymentData);
    try {
      payment.validate();
      return { isValid: true };
    } catch (error) {
      return { isValid: false, error: error.message };
    }
  }
}

export default new PaymentController();

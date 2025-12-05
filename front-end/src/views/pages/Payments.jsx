import React, { useEffect } from 'react';
import { useMVC } from '../../controllers/MVCContext';

export default function Payments() {
  const { state, actions } = useMVC();

  const payments = state.data.payments || [];
  const loading = state.loading.payments;
  const error = state.errors.payments;

  useEffect(() => {
    if (payments.length === 0 && !loading) {
      actions.payments.getPayments().catch(console.error);
    }
  }, [payments.length, loading, actions.payments]);

  if (error) return <div>Error: {error}</div>;
  if (loading) return <div>Loading payments...</div>;

  return (
    <div>
      <h2>Payments</h2>
      <div>Total: {payments.length}</div>
      {payments.length === 0 ? (
        <div>No payments found</div>
      ) : (
        <ul>
          {payments.map(p => (
            <li key={p.id}>
              {p.description || 'Payment'} — {actions.payments.formatAmount(p)} — {p.status}
              {p.dueDate && <span> (Due: {new Date(p.dueDate).toLocaleDateString()})</span>}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}


import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { AuthProvider } from './auth/AuthProvider';
import { MVCProvider } from './controllers/MVCContext';

const root = createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <MVCProvider>
        <AuthProvider>
          <App />
        </AuthProvider>
      </MVCProvider>
    </BrowserRouter>
  </React.StrictMode>
);


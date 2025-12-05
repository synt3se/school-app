import React from 'react';
import { Link } from 'react-router-dom';

export default function Landing() {
  return (
    <div style={{ padding: 24 }}>
      <h1>Design School</h1>
      <p>Добро пожаловать! Выберите действие:</p>
      <div style={{ display: 'flex', gap: 8 }}>
        <Link to="/login"><button>Войти</button></Link>
        <Link to="/register"><button>Зарегистрироваться</button></Link>
      </div>
    </div>
  );
}


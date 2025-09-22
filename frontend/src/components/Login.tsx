import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { User } from '../types';
import { styles } from '../styles';
import { API_ENDPOINTS } from '../config';

interface LoginProps {
  onLogin: (user: User) => void;
}

function Login({ onLogin }: LoginProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate(); 

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    try {
      const response = await fetch(API_ENDPOINTS.LOGIN, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      
      if (response.ok) {
        const { token } = await response.json();
        localStorage.setItem('user', JSON.stringify({ username, token }));
        onLogin({ username, token });
        navigate("/");
      } else {
        setError('Invalid credentials');
      }
    } catch (err) {
      setError('Connection error. Please try again.');
    }
  };

  return (
    <div style={styles.loginContainer}>
      <h2 style={styles.loginTitle}>Login to CodeSignal Learn Pastebin</h2>
      <form style={styles.loginForm} onSubmit={handleSubmit}>
        <input
          style={styles.input}
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          style={styles.input}
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {error && <div style={{ color: 'red', textAlign: 'center' }}>{error}</div>}
        <button type="submit" style={styles.button}>
          Login
        </button>
      </form>
      <div style={{ textAlign: 'center', marginTop: '10px' }}>
        Don't have an account?{' '}
        <Link to="/register" style={{ color: '#0070f3', textDecoration: 'none' }}>
          Register
        </Link>
      </div>
    </div>
  );
}

export default Login;
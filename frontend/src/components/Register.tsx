import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { styles } from '../styles';
import { API_ENDPOINTS } from '../config';

function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('user');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      const response = await fetch(API_ENDPOINTS.REGISTER, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, role }),
      });
      if (response.ok) {
        alert("Registration successful! Please log in.");
        navigate('/login');
      } else {
        const data = await response.json();
        setError(data.error || 'Registration failed');
      }
    } catch (err) {
      setError('Connection error. Please try again.');
    }
  };

  return (
    <div style={styles.loginContainer}>
      <h2 style={styles.loginTitle}>Register</h2>
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
        <select
          style={styles.select}
          value={role}
          onChange={(e) => setRole(e.target.value)}
        >
          <option value="user">User</option>
          <option value="admin">Admin</option>
        </select>
        {error && <div style={{ color: 'red', textAlign: 'center' }}>{error}</div>}
        <button type="submit" style={styles.button}>Register</button>
      </form>
    </div>
  );
}

export default Register;
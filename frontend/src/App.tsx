import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Editor from './components/Editor';
import Login from './components/Login';
import Register from './components/Register';
import SnippetList from './components/SnippetList';
import { User } from './types';
import { styles } from './styles';

function App() {
  const [user, setUser] = useState<User | null>(() => {
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      localStorage.removeItem('user');
    }
  }, [user]);

  const handleLogout = () => {
    setUser(null);
    window.location.href = '/login';
  };

  return (
    <Router>
      <div style={styles.container}>
        <header style={styles.header}>
          <div style={{ width: '100%', textAlign: 'center' }}>
            <h1>CodeSignal Learn Pastebin</h1>
          </div>
          {user && (
            <div style={styles.headerControls}>
              <SnippetList />
              <button 
                onClick={handleLogout} 
                style={{ ...styles.button, ...styles.logoutButton }}
              >
                Logout
              </button>
            </div>
          )}
        </header>
        <Routes>
          {user ? (
            <>
              <Route path="/" element={<Editor />} />
              <Route path="/snippet/:id" element={<Editor />} />
            </>
          ) : (
            <>
              <Route path="/login" element={<Login onLogin={setUser} />} />
              <Route path="/register" element={<Register />} />
              <Route path="*" element={<Login onLogin={setUser} />} />
            </>
          )}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { API_ENDPOINTS } from '../config';
import { Snippet } from '../types';
import { styles } from '../styles';

function SnippetList() {
  const [snippets, setSnippets] = useState<Snippet[]>([]);
  const navigate = useNavigate();
  const location = useLocation();

  const currentSnippetId = location.pathname.startsWith('/snippet/')
    ? location.pathname.split('/snippet/')[1]
    : '';

    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        if (!storedUser) {
            console.error("No user found in localStorage");
            return;
        }
    
        const token = JSON.parse(storedUser).token;
        if (!token) {
            console.error("No token found in user data");
            return;
        }
    
        fetch(API_ENDPOINTS.SNIPPETS, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Failed to fetch snippets: ${res.status}`);
                }
                return res.json();
            })
            .then(setSnippets)
            .catch(error => {
                console.error("Error fetching snippets:", error);
                if (error.message.includes('401')) {
                    navigate('/login');
                }
            });
    }, [location, navigate]);

  const handleSelect = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const snippetId = e.target.value;
    if (snippetId) {
      navigate(`/snippet/${snippetId}`);
    }
  };

  return (
    <div style={styles.snippetListContainer}>
      <label htmlFor="snippet-select">Your Snippets:</label>
      <select
        id="snippet-select"
        onChange={handleSelect}
        value={currentSnippetId}
      >
        <option value="" disabled>
          Select a snippet
        </option>
        {snippets.map(snippet => (
          <option key={snippet.id} value={snippet.id}>
            {snippet.title}
          </option>
        ))}
      </select>
    </div>
  );
}

export default SnippetList;
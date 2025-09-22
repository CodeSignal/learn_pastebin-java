import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { python } from '@codemirror/lang-python';
import { java } from '@codemirror/lang-java';
import { cpp } from '@codemirror/lang-cpp';
import { Snippet } from '../types';
import { styles } from '../styles';
import { API_ENDPOINTS } from '../config';

const languages = {
  typescript: javascript({ typescript: true }),
  javascript: javascript(),
  python: python(),
  java: java(),
  cpp: cpp()
};

const storedUser = localStorage.getItem('user');
const userId = storedUser ? JSON.parse(storedUser).id : '';

function Editor() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [snippet, setSnippet] = useState<Snippet>({
    userId: '',
    title: '',
    content: '',
    language: 'typescript'
  });
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (id) {
      fetch(API_ENDPOINTS.SNIPPET(id))
        .then(res => res.json())
        .then(setSnippet)
        .catch(error => console.error("Error fetching snippet:", error));
    }
  }, [id]);

  const handleSave = async () => {
    setSaving(true);
    try {
      const storedUser = localStorage.getItem('user');
      const token = storedUser ? JSON.parse(storedUser).token : '';
  
      const response = await fetch(API_ENDPOINTS.SNIPPETS, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(snippet),
      });
      if (!response.ok) {
        throw new Error('Failed to save snippet');
      }
      const savedSnippet = await response.json();
      navigate(`/snippet/${savedSnippet.id}`);
    } catch (error) {
      console.error('Error saving snippet:', error);
    } finally {
      setSaving(false);
    }
  };
  
  const handleDelete = async () => {
    if (!snippet.id) return;
    const confirmDelete = window.confirm('Are you sure you want to delete this snippet?');
    if (!confirmDelete) return;
    try {
      const response = await fetch(API_ENDPOINTS.SNIPPET(snippet.id), {
        method: 'DELETE',
      });
      if (!response.ok) {
        throw new Error('Failed to delete snippet');
      }
      setSnippet({ userId, title: '', content: '', language: 'typescript' });
      navigate('/');
    } catch (error) {
      console.error("Error deleting snippet:", error);
    }
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setSnippet(prev => ({
          ...prev,
          content: e.target?.result as string
        }));
      };
      reader.readAsText(file);
    }
  };

  return (
    <div style={styles.editorContainer}>
      <div style={styles.controls}>
        <input
          style={styles.input}
          type="text"
          placeholder="Title"
          value={snippet.title}
          onChange={(e) => setSnippet(prev => ({ ...prev, title: e.target.value }))}
        />
        <select
          style={styles.select}
          value={snippet.language}
          onChange={(e) => setSnippet(prev => ({ ...prev, language: e.target.value }))}
        >
          {Object.keys(languages).map(lang => (
            <option key={lang} value={lang}>{lang}</option>
          ))}
        </select>
        <label style={styles.fileLabel}>
          Upload File
          <input
            style={styles.fileInput}
            type="file"
            onChange={handleFileUpload}
          />
        </label>
        <button 
          style={styles.button}
          onClick={handleSave}
          disabled={saving}
        >
          {saving ? 'Saving...' : 'Save'}
        </button>
        {snippet.id && (
          <button 
            style={{ ...styles.button, ...styles.deleteButton }}
            onClick={handleDelete}
          >
            Delete
          </button>
        )}
      </div>
      
      <div style={styles.editorWrapper}>
        <CodeMirror
          value={snippet.content}
          height="600px"
          theme="dark"
          extensions={[languages[snippet.language as keyof typeof languages]]}
          onChange={(value) => setSnippet(prev => ({ ...prev, content: value }))}
        />
      </div>
    </div>
  );
}

export default Editor;
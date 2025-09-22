export const styles = {
    container: {
      maxWidth: '1200px',
      margin: '0 auto',
      padding: '20px',
      fontFamily: 'system-ui, -apple-system, sans-serif',
    },
    header: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: '20px',
      padding: '10px 0',
      borderBottom: '1px solid #eaeaea',
    },
    headerControls: {
      display: "flex",
      gap: "10px",
    },
    deleteButton: {
      backgroundColor: "#ff0000",
      color: "#fff",
    },
    snippetListContainer: {
      display: "flex",
      flexDirection: "column" as "column",
      gap: "10px",
    },
    editorContainer: {
      display: 'flex',
      flexDirection: 'column' as const,
      gap: '15px',
    },
    controls: {
      display: 'flex',
      gap: '10px',
      flexWrap: 'wrap' as const,
      alignItems: 'center',
    },
    input: {
      padding: '8px 12px',
      border: '1px solid #ddd',
      borderRadius: '4px',
      fontSize: '14px',
      flex: '1',
      minWidth: '200px',
    },
    select: {
      padding: '8px 12px',
      border: '1px solid #ddd',
      borderRadius: '4px',
      fontSize: '14px',
      backgroundColor: 'white',
    },
    button: {
      padding: '8px 16px',
      backgroundColor: '#0070f3',
      color: 'white',
      border: 'none',
      borderRadius: '4px',
      cursor: 'pointer',
      fontSize: '14px',
      transition: 'background-color 0.2s',
      '&:hover': {
        backgroundColor: '#0051cc',
      },
    },
    logoutButton: {
      backgroundColor: '#ff4444',
      '&:hover': {
        backgroundColor: '#cc0000',
      },
    },
    fileInput: {
      display: 'none',
    },
    fileLabel: {
      padding: '8px 16px',
      backgroundColor: '#28a745',
      color: 'white',
      borderRadius: '4px',
      cursor: 'pointer',
      fontSize: '14px',
      transition: 'background-color 0.2s',
      '&:hover': {
        backgroundColor: '#218838',
      },
    },
    loginContainer: {
      maxWidth: '400px',
      margin: '100px auto',
      padding: '20px',
      borderRadius: '8px',
      boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
      backgroundColor: 'white',
    },
    loginForm: {
      display: 'flex',
      flexDirection: 'column' as const,
      gap: '15px',
    },
    loginTitle: {
      textAlign: 'center' as const,
      marginBottom: '20px',
      color: '#333',
    },
    editorWrapper: {
      border: '1px solid #ddd',
      borderRadius: '4px',
      overflow: 'hidden',
    },
  };
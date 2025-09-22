# Pastebin Demo Application

A simple Pastebin-like application built with Spring Boot, React, TypeScript. This application is intentionally built with minimal security measures for educational purposes in security courses.

## Features

- Code snippet creation and editing
- Support for multiple programming languages:
  - TypeScript
  - JavaScript
  - Python
  - Java
  - C++
- Syntax highlighting using CodeMirror
- File upload functionality
- Basic user authentication
- Unique URLs for each saved snippet
- SQLite database with Hibernate/JPA

## ⚠️ Security Notice

This application is deliberately built WITHOUT security measures for educational purposes. It contains various vulnerabilities including but not limited to:
- SQL Injection possibilities
- No input validation
- Weak authentication
- No CSRF protection
- Potential XSS vulnerabilities

DO NOT USE THIS IN PRODUCTION!

## Prerequisites

- Java 17 (JDK)
- Maven 3.9+
- Node.js (v18 or higher)
- npm (Node Package Manager)

## Installation

1. Build the frontend:
```bash
cd frontend
npm install
npm run build
cd ..
```

2. Start the application (backend):
```bash
cd backend
mvn spring-boot:run
```

## Usage

1. Access the application at `http://localhost:3000`

2. Login with default credentials:
   - Username: `admin`
   - Password: `codesignal`

3. Create new snippets:
   - Enter a title
   - Select a programming language
   - Write or paste your code
   - Click "Save" to generate a unique URL

4. Upload files:
   - Click the file upload button
   - Select a text file
   - The content will be automatically loaded into the editor

5. Access saved snippets:
   - Use the generated URL (format: `/snippet/:id`)
   - Edit and save changes as needed

## Development

### Running in Development Mode
```bash
# Build frontend
cd frontend && npm install && npm run build && cd ..

# Run backend (will serve the built frontend)
cd backend && mvn spring-boot:run
```

The application runs on port 3000 by default.

### Troubleshooting

If you encounter issues:

1. **Java version issues**: Ensure you're using JDK 17 and Maven 3.9+
2. **Frontend build**: Rebuild if UI isn’t loading: `cd frontend && npm run build`
3. **Port already in use**: The app will try to use port 3000
4. **Database issues**: Delete `database.sqlite` file and restart

## API Endpoints

- `POST /api/auth/login` - User authentication
- `POST /api/auth/register` - User registration
- `POST /api/snippets` - Create/update snippets
- `GET /api/snippets/{id}` - Retrieve a specific snippet

## Contributing

This is a demo application for educational purposes. If you find any bugs or want to suggest improvements, please open an issue or submit a pull request.

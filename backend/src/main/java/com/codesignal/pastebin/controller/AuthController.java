package com.codesignal.pastebin.controller;

import com.codesignal.pastebin.model.Role;
import com.codesignal.pastebin.model.User;
import com.codesignal.pastebin.repo.UserRepository;
import com.codesignal.pastebin.util.ErrorResponse;
import com.codesignal.pastebin.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final JwtUtil jwt;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, JwtUtil jwt, PasswordEncoder encoder) {
        this.users = users;
        this.jwt = jwt;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String username = request.username();
        String password = request.password();
        String roleRaw = request.role() == null ? "user" : request.role();

        if (username == null || password == null) {
            return error(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        if (users.findByUsername(username).isPresent()) {
            return error(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        Role role = switch (roleRaw) {
            case "admin" -> Role.ADMIN;
            default -> Role.USER;
        };
        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode(password));
        u.setRole(role);
        users.save(u);

        return ResponseEntity.ok(new RegisterResponse("User registered successfully", u.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String username = request.username();
        String password = request.password();

        User user = users.findByUsername(username).orElse(null);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwt.generateTokenWithUserId(user.getId());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String detail) {
        return ResponseEntity.status(status).body(new ErrorResponse(detail));
    }

    public record RegisterRequest(String username, String password, String role) {
    }

    public record LoginRequest(String username, String password) {
    }

    public record RegisterResponse(String message, Integer userId) {
    }

    public record TokenResponse(String token) {
    }
}

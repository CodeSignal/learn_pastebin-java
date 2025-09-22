package com.example.pastebin.controller;

import com.example.pastebin.model.Role;
import com.example.pastebin.model.User;
import com.example.pastebin.repo.UserRepository;
import com.example.pastebin.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final JwtUtil jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository users, JwtUtil jwt) {
        this.users = users;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String roleRaw = (String) body.getOrDefault("role", "user");

        if (username == null || password == null) {
            return error(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        if (users.findByUsername(username).isPresent()) {
            return error(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        Role role = "admin".equals(roleRaw) ? Role.ADMIN : Role.USER;
        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode(password));
        u.setRole(role);
        users.save(u);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "User registered successfully");
        res.put("userId", u.getId());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        var userOpt = users.findByUsername(username);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (encoder.matches(password, u.getPassword())) {
                String token = jwt.generateTokenWithUserId(u.getId());
                Map<String, Object> res = new HashMap<>();
                res.put("token", token);
                return ResponseEntity.ok(res);
            }
        }
        return error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String detail) {
        Map<String, Object> err = new HashMap<>();
        err.put("detail", detail);
        return ResponseEntity.status(status).body(err);
    }
}


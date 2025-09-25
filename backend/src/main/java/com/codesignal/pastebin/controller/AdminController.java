package com.codesignal.pastebin.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.codesignal.pastebin.model.Role;
import com.codesignal.pastebin.model.User;
import com.codesignal.pastebin.repo.UserRepository;
import com.codesignal.pastebin.util.ErrorResponse;
import com.codesignal.pastebin.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users;
    private final JwtUtil jwt;
    private final DataSource dataSource;

    public AdminController(UserRepository users, JwtUtil jwt, DataSource dataSource) {
        this.users = users;
        this.jwt = jwt;
        this.dataSource = dataSource;
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String detail) {
        return ResponseEntity.status(status).body(new ErrorResponse(detail));
    }

    private AdminOutcome verifyAdminOrError(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return new AdminOutcome(null, error(HttpStatus.UNAUTHORIZED, "Missing authorization header"));
        }
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        try {
            DecodedJWT decoded = jwt.verify(token);
            Integer userId = decoded.getClaim("userId").asInt();
            var userOpt = users.findById(userId);
            if (userOpt.isEmpty()) {
                return new AdminOutcome(null, error(HttpStatus.UNAUTHORIZED, "User not found"));
            }
            User u = userOpt.get();
            if (u.getRole() != Role.ADMIN) {
                return new AdminOutcome(null, error(HttpStatus.FORBIDDEN, "Access denied"));
            }
            return new AdminOutcome(u, null);
        } catch (Exception e) {
            return new AdminOutcome(null, error(HttpStatus.UNAUTHORIZED, "Invalid token"));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> adminTest(@RequestHeader(value = "authorization", required = false) String authorization) {
        var outcome = verifyAdminOrError(authorization);
        if (outcome.error() != null) return outcome.error();
        return ResponseEntity.ok(Map.of("message", "Admin test endpoint accessed successfully"));
    }

    @GetMapping("/testOpen")
    public Map<String, Object> adminTestOpen() {
        return Map.of("message", "Test endpoint is working!");
    }

    @GetMapping("/accountInfo")
    public ResponseEntity<?> accountInfo(@RequestParam(name = "id", required = false) String id) {
        if (id == null || id.isBlank()) {
            return error(HttpStatus.BAD_REQUEST, "Missing user ID parameter");
        }

        String sql = "SELECT * FROM users WHERE id = " + id;
        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Map<String, Object>> out = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(md.getColumnLabel(i), rs.getObject(i));
                }
                out.add(row);
            }
            return ResponseEntity.ok(out);
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private record AdminOutcome(User user, ResponseEntity<ErrorResponse> error) {
    }
}

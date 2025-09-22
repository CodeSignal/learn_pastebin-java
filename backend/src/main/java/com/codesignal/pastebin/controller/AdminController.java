package com.example.pastebin.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.pastebin.model.Role;
import com.example.pastebin.model.User;
import com.example.pastebin.repo.UserRepository;
import com.example.pastebin.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

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

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String detail) {
        Map<String, Object> err = new HashMap<>();
        err.put("detail", detail);
        return ResponseEntity.status(status).body(err);
    }

    private Object[] verifyAdminOrError(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return new Object[]{null, error(HttpStatus.UNAUTHORIZED, "Missing authorization header")};
        }
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        try {
            DecodedJWT decoded = jwt.verify(token);
            Integer userId = decoded.getClaim("userId").asInt();
            var userOpt = users.findById(userId);
            if (userOpt.isEmpty()) {
                return new Object[]{null, error(HttpStatus.UNAUTHORIZED, "User not found")};
            }
            User u = userOpt.get();
            if (u.getRole() != Role.ADMIN) {
                return new Object[]{null, error(HttpStatus.FORBIDDEN, "Access denied")};
            }
            return new Object[]{u, null};
        } catch (Exception e) {
            return new Object[]{null, error(HttpStatus.UNAUTHORIZED, "Invalid token")};
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> adminTest(@RequestHeader(value = "authorization", required = false) String authorization) {
        Object[] res = verifyAdminOrError(authorization);
        if (res[1] != null) return (ResponseEntity<?>) res[1];
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

        // Deliberately vulnerable: Direct string concatenation into SQL
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
}

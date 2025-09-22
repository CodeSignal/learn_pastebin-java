package com.example.pastebin.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.pastebin.model.Snippet;
import com.example.pastebin.model.User;
import com.example.pastebin.repo.SnippetRepository;
import com.example.pastebin.repo.UserRepository;
import com.example.pastebin.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {
    private final SnippetRepository snippets;
    private final UserRepository users;
    private final JwtUtil jwt;

    public SnippetController(SnippetRepository snippets, UserRepository users, JwtUtil jwt) {
        this.snippets = snippets;
        this.users = users;
        this.jwt = jwt;
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String detail) {
        Map<String, Object> err = new HashMap<>();
        err.put("detail", detail);
        return ResponseEntity.status(status).body(err);
    }

    private Object[] getCurrentUserOrError(String authorizationHeader) {
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
            return new Object[]{userOpt.get(), null};
        } catch (Exception e) {
            return new Object[]{null, error(HttpStatus.UNAUTHORIZED, "Invalid token")};
        }
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestHeader(value = "authorization", required = false) String authorization,
                                    @RequestBody Map<String, Object> body) {
        Object[] resUser = getCurrentUserOrError(authorization);
        if (resUser[1] != null) return (ResponseEntity<?>) resUser[1];
        User user = (User) resUser[0];
        Snippet s = new Snippet();
        s.setId(UUID.randomUUID().toString());
        s.setTitle((String) body.get("title"));
        s.setContent((String) body.get("content"));
        s.setLanguage((String) body.get("language"));
        s.setUserId(user.getId());
        s = snippets.save(s);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", s.getId());
        res.put("title", s.getTitle());
        res.put("content", s.getContent());
        res.put("language", s.getLanguage());
        res.put("userId", String.valueOf(s.getUserId()));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") String id) {
        return snippets.findById(id)
                .<ResponseEntity<?>>map(s -> {
                    Map<String, Object> res = new LinkedHashMap<>();
                    res.put("id", s.getId());
                    res.put("title", s.getTitle());
                    res.put("content", s.getContent());
                    res.put("language", s.getLanguage());
                    res.put("userId", String.valueOf(s.getUserId()));
                    return ResponseEntity.ok(res);
                })
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "Snippet not found"));
    }

    @GetMapping("")
    public ResponseEntity<?> listForUser(@RequestHeader(value = "authorization", required = false) String authorization) {
        Object[] resUser = getCurrentUserOrError(authorization);
        if (resUser[1] != null) return (ResponseEntity<?>) resUser[1];
        Integer uid = ((User) resUser[0]).getId();
        var list = snippets.findByUserId(uid).stream().map(s -> Map.of(
                "id", s.getId(),
                "title", s.getTitle(),
                "language", s.getLanguage()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        return snippets.findById(id)
                .<ResponseEntity<?>>map(s -> {
                    snippets.delete(s);
                    return ResponseEntity.ok(Map.of("message", "Snippet deleted successfully"));
                })
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "Snippet not found"));
    }
}

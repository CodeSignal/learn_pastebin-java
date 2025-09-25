package com.codesignal.pastebin.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.codesignal.pastebin.model.Snippet;
import com.codesignal.pastebin.model.User;
import com.codesignal.pastebin.repo.SnippetRepository;
import com.codesignal.pastebin.repo.UserRepository;
import com.codesignal.pastebin.util.ErrorResponse;
import com.codesignal.pastebin.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
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

    @PostMapping("")
    public ResponseEntity<?> create(@RequestHeader(value = "authorization", required = false) String authorization,
                                    @RequestBody CreateSnippetRequest request) {
        var outcome = getCurrentUser(authorization);
        if (outcome.error() != null) return outcome.error();
        User user = outcome.user();

        Snippet s = new Snippet();
        s.setId(UUID.randomUUID().toString());
        s.setTitle(request.title());
        s.setContent(request.content());
        s.setLanguage(request.language());
        s.setUserId(user.getId());
        s = snippets.save(s);

        return ResponseEntity.ok(new SnippetResponse(s.getId(), s.getTitle(), s.getContent(), s.getLanguage(), String.valueOf(s.getUserId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") String id) {
        return snippets.findById(id)
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(new SnippetResponse(
                        s.getId(),
                        s.getTitle(),
                        s.getContent(),
                        s.getLanguage(),
                        String.valueOf(s.getUserId())
                )))
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "Snippet not found"));
    }

    @GetMapping("")
    public ResponseEntity<?> listForUser(@RequestHeader(value = "authorization", required = false) String authorization) {
        var outcome = getCurrentUser(authorization);
        if (outcome.error() != null) return outcome.error();
        Integer uid = outcome.user().getId();
        List<SnippetSummary> list = snippets.findByUserId(uid).stream()
                .map(s -> new SnippetSummary(s.getId(), s.getTitle(), s.getLanguage()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        return snippets.findById(id)
                .<ResponseEntity<?>>map(s -> {
                    snippets.delete(s);
                    return ResponseEntity.ok(new DeleteResponse("Snippet deleted successfully"));
                })
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "Snippet not found"));
    }

    private AuthOutcome getCurrentUser(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return new AuthOutcome(null, error(HttpStatus.UNAUTHORIZED, "Missing authorization header"));
        }
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        try {
            DecodedJWT decoded = jwt.verify(token);
            Integer userId = decoded.getClaim("userId").asInt();
            return users.findById(userId)
                    .map(user -> new AuthOutcome(user, null))
                    .orElseGet(() -> new AuthOutcome(null, error(HttpStatus.UNAUTHORIZED, "User not found")));
        } catch (Exception e) {
            return new AuthOutcome(null, error(HttpStatus.UNAUTHORIZED, "Invalid token"));
        }
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String detail) {
        return ResponseEntity.status(status).body(new ErrorResponse(detail));
    }

    private record AuthOutcome(User user, ResponseEntity<ErrorResponse> error) {
    }

    public record CreateSnippetRequest(String title, String content, String language) {
    }

    public record SnippetResponse(String id, String title, String content, String language, String userId) {
    }

    public record SnippetSummary(String id, String title, String language) {
    }

    public record DeleteResponse(String message) {
    }
}

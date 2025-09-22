package com.example.pastebin.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class FrontendController {
    private File resolveIndexHtml() {
        // When running from repo root
        File local = new File("frontend/dist/index.html");
        if (local.exists()) return local;

        // When running from backend directory
        File sibling = new File("../frontend/dist/index.html");
        if (sibling.exists()) return sibling;

        // When running in Docker image
        File container = new File("/app/frontend/dist/index.html");
        if (container.exists()) return container;
        return null;
    }
    private File resolveStatic(String relPath) {
        if (relPath == null || relPath.isBlank()) return null;
        if (relPath.startsWith("/")) relPath = relPath.substring(1);

        // When running from repo root
        File local = new File("frontend/dist/" + relPath);
        if (local.exists() && local.isFile()) return local;

        // When running from backend directory
        File sibling = new File("../frontend/dist/" + relPath);
        if (sibling.exists() && sibling.isFile()) return sibling;

        // When running in Docker image
        File container = new File("/app/frontend/dist/" + relPath);
        if (container.exists() && container.isFile()) return container;
        return null;
    }

    @GetMapping("/")
    public ResponseEntity<?> root() {
        File f = resolveIndexHtml();
        if (f != null) return ResponseEntity.ok(new FileSystemResource(f));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Use Spring PathPattern trailing wildcard capture syntax
    @GetMapping("/{*fullPath}")
    public ResponseEntity<?> serveApp(@PathVariable("fullPath") String fullPath) {
        if (fullPath != null && fullPath.startsWith("api/")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        File direct = resolveStatic(fullPath);
        if (direct != null) {
            var mediaType = MediaTypeFactory.getMediaType(direct.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(new FileSystemResource(direct));
        }
        File f = resolveIndexHtml();
        if (f != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(new FileSystemResource(f));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

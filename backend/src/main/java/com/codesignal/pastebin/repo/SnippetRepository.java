package com.example.pastebin.repo;

import com.example.pastebin.model.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnippetRepository extends JpaRepository<Snippet, String> {
    List<Snippet> findByUserId(Integer userId);
}


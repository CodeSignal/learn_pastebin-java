package com.codesignal.pastebin.repo;

import com.codesignal.pastebin.model.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnippetRepository extends JpaRepository<Snippet, String> {
    List<Snippet> findByUserId(Integer userId);
}


package com.codesignal.pastebin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "snippets")
public class Snippet {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String language = "typescript";

    @Column(name = "userId", nullable = false)
    private Integer userId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}


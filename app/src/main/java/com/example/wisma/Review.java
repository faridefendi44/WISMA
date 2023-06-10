package com.example.wisma;

import java.io.Serializable;

public class Review implements Serializable {
    private String id;
    private String author;
    private String description;

    public Review() {
    }

    public Review(String id, String author, String description) {
        this.id = id;
        this.author = author;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
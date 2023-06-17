package com.example.wisma.model;

import java.io.Serializable;

public class ModelReview implements Serializable {
    private String id;
    private String author;
    private String location;
    private String description;

    private String imgURL;

    public ModelReview() {
    }

    public ModelReview(String id, String author, String location, String description, String imgURL) {
        this.id = id;
        this.author = author;
        this.location = location;
        this.description = description;
        this.imgURL = imgURL;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
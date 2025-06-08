package com.example.itp4229m.model;

public class ReviewRequest {
    private int rating;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    private String comment;

    // Constructor, Getters and Setters
    public ReviewRequest(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}

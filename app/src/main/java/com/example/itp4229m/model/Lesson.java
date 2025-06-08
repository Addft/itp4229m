package com.example.itp4229m.model;

public class Lesson {
    private int id;
    private String title;
    private int order;
    private String duration;  // Changed from int to String to handle time format "00:34:00"

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    // Getters and Setters
}

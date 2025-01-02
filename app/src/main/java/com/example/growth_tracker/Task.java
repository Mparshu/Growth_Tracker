package com.example.growth_tracker;

public class Task {
    private long id;
    private String description;
    private int score;
    private boolean isCompleted;
    private TaskArea area;

    public Task(String description, int score, TaskArea area) {
        this.description = description;
        this.score = score;
        this.area = area;
        this.isCompleted = false;
    }

    public TaskArea getArea() {
        return area;
    }

    public void setArea(TaskArea area) {
        this.area = area;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public int getScore() {
        return score;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }



    @Override
    public String toString() {
        return description + " (Score: " + score + ")" + (isCompleted ? " ✓" : "");
    }
}

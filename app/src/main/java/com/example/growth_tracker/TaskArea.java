package com.example.growth_tracker;

public enum TaskArea {
    PHYSICAL("Physical Activities"),
    MENTAL("Mental Activities"),
    EMOTIONAL("Emotional Skills"),
    FINANCIAL("Financial Skills");

    private final String displayName;

    TaskArea(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

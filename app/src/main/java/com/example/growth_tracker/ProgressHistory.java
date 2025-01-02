// ProgressHistory.java
package com.example.growth_tracker;

import java.util.Date;

public class ProgressHistory {
    private long id;
    private Date date;
    private int physicalScore;
    private int mentalScore;
    private int emotionalScore;
    private int financialScore;
    private int physicalTasksTotal;
    private int mentalTasksTotal;
    private int emotionalTasksTotal;
    private int financialTasksTotal;
    private int physicalTasksCompleted;
    private int mentalTasksCompleted;
    private int emotionalTasksCompleted;
    private int financialTasksCompleted;

    public ProgressHistory() {
        this.date = new Date();
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getPhysicalScore() { return physicalScore; }
    public void setPhysicalScore(int physicalScore) { this.physicalScore = physicalScore; }

    public int getMentalScore() { return mentalScore; }
    public void setMentalScore(int mentalScore) { this.mentalScore = mentalScore; }

    public int getEmotionalScore() { return emotionalScore; }
    public void setEmotionalScore(int emotionalScore) { this.emotionalScore = emotionalScore; }

    public int getFinancialScore() { return financialScore; }
    public void setFinancialScore(int financialScore) { this.financialScore = financialScore; }

    public int getPhysicalTasksTotal() { return physicalTasksTotal; }
    public void setPhysicalTasksTotal(int physicalTasksTotal) { this.physicalTasksTotal = physicalTasksTotal; }

    public int getMentalTasksTotal() { return mentalTasksTotal; }
    public void setMentalTasksTotal(int mentalTasksTotal) { this.mentalTasksTotal = mentalTasksTotal; }

    public int getEmotionalTasksTotal() { return emotionalTasksTotal; }
    public void setEmotionalTasksTotal(int emotionalTasksTotal) { this.emotionalTasksTotal = emotionalTasksTotal; }

    public int getFinancialTasksTotal() { return financialTasksTotal; }
    public void setFinancialTasksTotal(int financialTasksTotal) { this.financialTasksTotal = financialTasksTotal; }

    public int getPhysicalTasksCompleted() { return physicalTasksCompleted; }
    public void setPhysicalTasksCompleted(int physicalTasksCompleted) { this.physicalTasksCompleted = physicalTasksCompleted; }

    public int getMentalTasksCompleted() { return mentalTasksCompleted; }
    public void setMentalTasksCompleted(int mentalTasksCompleted) { this.mentalTasksCompleted = mentalTasksCompleted; }

    public int getEmotionalTasksCompleted() { return emotionalTasksCompleted; }
    public void setEmotionalTasksCompleted(int emotionalTasksCompleted) { this.emotionalTasksCompleted = emotionalTasksCompleted; }

    public int getFinancialTasksCompleted() { return financialTasksCompleted; }
    public void setFinancialTasksCompleted(int financialTasksCompleted) { this.financialTasksCompleted = financialTasksCompleted; }

    public int getTotalScore() {
        return physicalScore + mentalScore + emotionalScore + financialScore;
    }
}
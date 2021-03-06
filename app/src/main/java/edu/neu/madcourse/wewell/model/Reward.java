package edu.neu.madcourse.wewell.model;

public class Reward {
    private String title;
    private int type;
    private long progress;
    private double goal;
    private double finishedAmount;
    private boolean isFinished;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public double getFinishedAmount() {
        return finishedAmount;
    }

    public void setFinishedAmount(double finishedAmount) {
        this.finishedAmount = finishedAmount;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}

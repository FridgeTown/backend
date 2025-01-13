package com.sparta.fritown.domain.websocket.model;


public class UserStats {
    private int finalPunch;
    private double avgHeartRate;
    private int finalCalorie;
    private int heartRateCount;

    public UserStats() {
        this.finalPunch = 0;
        this.avgHeartRate = 0;
        this.finalCalorie = 0;
        this.heartRateCount = 0;
    }

    public void updateStats(int punch, double heartRate, int calories) {
        this.finalPunch += punch;
        this.avgHeartRate = (this.avgHeartRate * heartRateCount + heartRate) / (++heartRateCount);
        this.finalCalorie += calories;
    }

    public int getFinalPunch() {
        return finalPunch;
    }

    public double getAvgHeartRate() {
        return avgHeartRate;
    }

    public int getFinalCalorie() {
        return finalCalorie;
    }

    @Override
    public String toString() {
        return "UserStats{" +
                "finalPunch=" + finalPunch +
                ", avgHeartRate=" + avgHeartRate +
                ", finalCalorie=" + finalCalorie +
                '}';
    }
}
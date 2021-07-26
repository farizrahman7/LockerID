package com.example.lokerid.Model;

public class HistoryModel {
    private String stand;
    private String time;
    private String loker;

    public HistoryModel(String loker, String time, String stand) {
        this.stand = stand;
        this.time = time;
        this.loker = loker;
    }

    public HistoryModel() {
    }

    public String getStand() {
        return stand;
    }

    public void setStand(String stand) {
        this.stand = stand;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLoker() {
        return loker;
    }

    public void setLoker(String loker) {
        this.loker = loker;
    }
}

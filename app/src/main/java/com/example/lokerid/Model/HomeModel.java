package com.example.lokerid.Model;

public class HomeModel {
    private String loker;
    private String stand;
    private long time;

    public HomeModel(String loker, String stand, long time) {
        this.loker = loker;
        this.stand = stand;
        this.time = time;
    }

    public HomeModel() {
    }

    public String getLoker() {
        return loker;
    }

    public void setLoker(String loker) {
        this.loker = loker;
    }

    public String getStand() {
        return stand;
    }

    public void setStand(String stand) {
        this.stand = stand;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

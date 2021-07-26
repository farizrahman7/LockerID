package com.example.lokerid.Model;

public class LokerModel {
    private int id;
    private String status;

    public LokerModel() {
    }

    public LokerModel(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

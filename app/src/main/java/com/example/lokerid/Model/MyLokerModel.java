package com.example.lokerid.Model;

public class MyLokerModel {
    private String loker;
    private String tanggal;
    private String jam;
    private String kamera;

    public MyLokerModel(String loker, String tanggal, String jam,String kamera) {
        this.loker = loker;
        this.tanggal = tanggal;
        this.jam = jam;
        this.kamera = kamera;
    }

    public MyLokerModel() {
    }

    public MyLokerModel(String s, String s1, String s2) {
    }

    public String getLoker() {
        return loker;
    }

    public void setLoker(String loker) {
        this.loker = loker;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getKamera() { return kamera; }

    public void setKamera(String kamera) { this.kamera = kamera; }
}

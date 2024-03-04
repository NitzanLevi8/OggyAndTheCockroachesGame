package com.example.oggyandthecockroaches;

public class Player {

    private String name;
    private int score;

    private double latitude;
    private double longitude;


    public Player(String name, int score, double latitude, double longitude) {
        this.name = name;
        this.score = score;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

}

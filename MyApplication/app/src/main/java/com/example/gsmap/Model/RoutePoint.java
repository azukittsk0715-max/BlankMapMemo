package com.example.gsmap.Model;

public class RoutePoint {
    private double latitude;
    private double longitude;
    private String c_time;

    public RoutePoint(double latitude, double longitude, String c_time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.c_time = c_time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCTime() {
        return c_time;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCTime(String c_time) {
        this.c_time = c_time;
    }
}
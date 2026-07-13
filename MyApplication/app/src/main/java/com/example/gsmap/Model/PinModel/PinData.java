package com.example.gsmap.Model.PinModel;

public class PinData {

    private final double latitude;
    private final double longitude;
    private final String memo;

    public PinData(
            double latitude,
            double longitude,
            String memo) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.memo = memo;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMemo() {
        return memo;
    }
}
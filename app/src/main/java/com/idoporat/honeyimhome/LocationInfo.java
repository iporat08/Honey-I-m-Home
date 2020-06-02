package com.idoporat.honeyimhome;

import android.content.Context;

public class LocationInfo {
    private double latitude;
    private double longitude;
    private float accuracy;

    ////////////////////////////////// Getters /////////////////////////////////////////////////////
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    ////////////////////////////////// Setters /////////////////////////////////////////////////////
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}

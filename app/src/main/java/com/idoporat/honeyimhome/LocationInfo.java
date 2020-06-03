package com.idoporat.honeyimhome;

import android.content.Context;

public class LocationInfo {
    private double latitude;
    private double longitude;
    private float accuracy;

    /**
     * default constructor
     */
    LocationInfo(){}

    /**
     * todo
     * @param other
     */
    LocationInfo(LocationInfo other){
        latitude = other.getLatitude();
       longitude = other.getLongitude();
    }

    ////////////////////////////////// Getters /////////////////////////////////////////////////////

    /**
     * todo
     * @return
     */
    double getLatitude() {
        return latitude;
    }

    /**
     * todo
     * @return
     */
    double getLongitude() {
        return longitude;
    }

    /**
     * todo
     * @return
     */
    float getAccuracy() {
        return accuracy;
    }

    ////////////////////////////////// Setters /////////////////////////////////////////////////////

    /**
     * todo
     * @param latitude
     */
    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * todo
     * @param longitude
     */
    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * todo
     * @param accuracy
     */
    void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}

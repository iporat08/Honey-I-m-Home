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
     * Copy constructor
     * @param other another LocationInfo object
     */
    LocationInfo(LocationInfo other){
        latitude = other.getLatitude();
        longitude = other.getLongitude();
    }

    ////////////////////////////////// Getters /////////////////////////////////////////////////////

    /**
     * @return latitude
     */
    double getLatitude() {
        return latitude;
    }

    /**
     * @return longitude
     */
    double getLongitude() {
        return longitude;
    }

    /**
     * @return accuracy
     */
    float getAccuracy() {
        return accuracy;
    }

    ////////////////////////////////// Setters /////////////////////////////////////////////////////

    /**
     * Sets this.latitude to be newLatitude
     * @param newLatitude the new latitude
     */
    void setLatitude(double newLatitude) {
       latitude = newLatitude;
    }

    /**
     * Sets longitude to be newLongitude
     * @param newLongitude the new longitude
     */
    void setLongitude(double newLongitude) {
        longitude = newLongitude;
    }

    /**
     * Sets accuracy to be newAccuracy
     * @param newAccuracy the new accuracy
     */
    void setAccuracy(float newAccuracy) {
        accuracy = newAccuracy;
    }
}

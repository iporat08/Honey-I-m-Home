package com.idoporat.honeyimhome;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class LocationTracker {

    private static final String STOPPED_2 = "stopped_2";
    ////////////////////////////////////// Constants ///////////////////////////////////////////////
    private static final String LOCATION = "location";
    private final static String TAG = "lack of permission";
    private final static String PERMISSION_ERROR_MSG = "app dose'nt have location permission";
    private static final String STARTED = "started";
    private static final String STOPPED = "stopped";
    private static final String NEW_LOCATION = "new_location";
    private static final String NEW_LOCATION_2 = "new_location_2";
    private static final String NO_LOCATION = "no_location";

    ////////////////////////////////////// Data Members ////////////////////////////////////////////
    private Context context;
    private LocationInfo homeLocation;
    private LocationInfo locationInfo;
    private boolean locating;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private final int receiver;

    private final static int APPLICATION = 0;
    private final static int ACTIVITY = 1;


    ///////////////////////////////////// Constructors /////////////////////////////////////////////
    /**
     * Constructor
     * @param context a context
     */
    LocationTracker(Context context, int receiver){
        this.receiver = receiver;
        this.context = context;
        mFusedLocationClient = getFusedLocationProviderClient(context);
        locationInfo = new LocationInfo();
        locating = false;
    }

    /////////////////////////////////// Tracking Logic Methods /////////////////////////////////////
    /**
     * Starts tracking location
     */
    void startTracking(){
        if(hasPermissions()) {
            if(isLocationEnabled()){
                if(mFusedLocationClient == null){
                    mFusedLocationClient = getFusedLocationProviderClient(context);
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                requestNewLocationData();
                            }
                        });
            }
            else{
                if(receiver == ACTIVITY) {
                    Intent noLocationIntent = new Intent();
                    noLocationIntent.setAction(NO_LOCATION);
                    context.sendBroadcast(noLocationIntent);
                }
            }
        }
        else {
            Log.d(TAG, PERMISSION_ERROR_MSG);
        }
    }

    /**
     * Stops tracking the user's location
     */
    void stopTracking(){
        if(mFusedLocationClient != null){
            if(locationCallback != null) {
                mFusedLocationClient.removeLocationUpdates(locationCallback);
                mFusedLocationClient = null;
            }
        }
        locating = false;
        String action = receiver == ACTIVITY ? STOPPED : STOPPED_2;
        Intent noLocationIntent = new Intent();
        noLocationIntent.setAction(action);
        context.sendBroadcast(noLocationIntent);
    }

    /**
     * Handles location change of the device
     * @param location a Location object representing the net location
     */
    private void onLocationChanged(Location location) {
        locationInfo.setAccuracy(location.getAccuracy());
        locationInfo.setLatitude(location.getLatitude());
        locationInfo.setLongitude(location.getLongitude());

        Intent locationIntent = new Intent();

        if(!locating){
            String action = (receiver == ACTIVITY) ? STARTED : NEW_LOCATION_2;
            locating = true;
            locationIntent.setAction(action);
            locationIntent.putExtra(LOCATION, location);
        }
        else {
            String action = (receiver == ACTIVITY) ? NEW_LOCATION : NEW_LOCATION_2;
            locationIntent.setAction(action);
            locationIntent.putExtra(LOCATION, location);
        }
        context.sendBroadcast(locationIntent);
    }

    /**
     * Requests location updates periodically
     */
    private void requestNewLocationData(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                Looper.myLooper());
        }

    ///////////////////////////////////// Self Checks //////////////////////////////////////////////
    /**
     * Checks whether or not the app has location permissions
     * @return true if it has, false otherwise.
     */
    private boolean hasPermissions(){
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks whether or not the location services are enabled.
     * @return true if yes, false otherwise.
     */
    private boolean isLocationEnabled(){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled && network_enabled;
    }

    ///////////////////////////////////////// Setters //////////////////////////////////////////////
    /**
     * Updates the homeLocation according to the latest location
     */
    void updateHome(){
        homeLocation = new LocationInfo(locationInfo);
    }

    /**
     * Updates the homeLocation to be newHome.
     * @param newHome the new homeLocation to be
     */
    void updateHome(LocationInfo newHome){
        homeLocation = newHome;
    }

    /**
     * Replaces homeLocation with null.
     */
    void deleteHome(){
        homeLocation = null;
    }

    ///////////////////////////////////////// Getters //////////////////////////////////////////////
    /**
     * Returns locationInfo
     */
    LocationInfo getLocationInfo(){
        return locationInfo;
    }

    /**
     * Returns homeLocation
     */
    LocationInfo getHomeLocation() {
        return homeLocation;
    }


}

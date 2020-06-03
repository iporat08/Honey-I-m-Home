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

    private Context context;
    private LocationInfo homeLocation;
    private LocationInfo locationInfo;
    private boolean locating;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;

    private final static String TAG = "lack of permission";
    private final static String PERMISSION_ERROR_MSG = "app dose'nt have location permission";
    private static final String STARTED = "started";
    private static final String STOPPED = "stopped";
    private static final String NEW_LOCATION = "new_location";
    private static final String NO_LOCATION = "no_location";


    LocationTracker(Context context){
        this.context = context;
        mFusedLocationClient = getFusedLocationProviderClient(context);
        locationInfo = new LocationInfo();
        locating = false;
    }

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
                Intent noLocationIntent = new Intent();
                noLocationIntent.setAction(NO_LOCATION);
                context.sendBroadcast(noLocationIntent);
            }
        }
        else {
            Log.d(TAG, PERMISSION_ERROR_MSG);
            //todo send broadcast?
        }
    }

    void onLocationChanged(Location location) {
        locationInfo.setAccuracy(location.getAccuracy());
        locationInfo.setLatitude(location.getLatitude());
        locationInfo.setLongitude(location.getLongitude());

        Intent locationIntent = new Intent();
        if(!locating){
            locating = true;
            locationIntent.setAction(STARTED);
            locationIntent.putExtra(context.getString(R.string.location_intent_key), location); //todo static?
        }
        else {
            locationIntent.setAction(NEW_LOCATION);
            locationIntent.putExtra(context.getString(R.string.location_intent_key), location); //todo static?
        }
        context.sendBroadcast(locationIntent);
    }

    /**
     * todo
     */
    void stopTracking(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(locationCallback);
            mFusedLocationClient = null;
        }
        locating = false;
        Intent noLocationIntent = new Intent();
        noLocationIntent.setAction(STOPPED);
        context.sendBroadcast(noLocationIntent);
    }

    /**
     * todo
     * @return
     */
    private boolean hasPermissions(){
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * todo
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

    /**
     * todo
     * @return
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

    /**
     * todo
     */
    void updateHome(){
        homeLocation = new LocationInfo(locationInfo);
    }

    /**
     * todo
     * @param newHome
     */
    void updateHome(LocationInfo newHome){
        homeLocation = newHome;
    }

    /**
     * todo
     */
    void deleteHome(){
        homeLocation = null;
    }

    //////////////////////////////////// Getters ///////////////////////////////////////////////////

    /**
     * todo
     * @return
     */
    LocationInfo getLocationInfo(){
        return locationInfo;
    }

    /**
     * todo
     * @return
     */
    LocationInfo getHomeLocation() {
        return homeLocation;
    }


}

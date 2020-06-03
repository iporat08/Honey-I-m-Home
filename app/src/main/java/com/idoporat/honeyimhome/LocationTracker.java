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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class LocationTracker {

    private Context context;
//    private BroadcastReceiver br;
    public LocationInfo locationInfo;
    private boolean locationEnabled;
    private FusedLocationProviderClient mFusedLocationClient;

    private final static String TAG = "lack of permission";
    private final static String PERMISSION_ERROR_MSG = "app dose'nt have location permission";


    LocationTracker(Context context){
        this.context = context;
        mFusedLocationClient = getFusedLocationProviderClient(context);
        locationInfo = new LocationInfo();
//        br = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if(LocationResult.hasResult(intent)){}
//                Intent location = new Intent();
//                intent.setAction("location_update");
////                intent.putExtra()
//
//            }
//        };
    }

    public boolean startTracking(){
        // todo - will start tracking the location and send a "started" broadcast intent

        if(hasPermissions()) {
            if(isLocationEnabled()){
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                requestNewLocationData();
                            }
                        });
                return true;
            }
        }


        Log.d(TAG, PERMISSION_ERROR_MSG);
        return false;

        // todo - The LocationTracker class should assume it has anything it needs in order to run.
        //  but just to be on the safe side, in the LocationTracker's "startTracking()" method,
        //  add a basic check that assert you have the runtime location permission. If yes continue
        //  to track location, if not just log some error to logcat and don't do anything (optional
        //  -send an "error" broadcast and listen to it in the activity would and show some error in
        //  the UI).
    }

    public void onLocationChanged(Location location) {
        locationInfo.setAccuracy(location.getAccuracy());
        locationInfo.setLatitude(location.getLatitude());
        locationInfo.setLongitude(location.getLongitude());

        Intent locationIntent = new Intent();
        locationIntent.setAction(context.getString(R.string.new_location));
        locationIntent.putExtra(context.getString(R.string.location_intent_key), location);
        context.sendBroadcast(locationIntent);
    }

    public void stopTracking(){
        //todo - will stop tracking and send a "stopped" broadcast intent
    }

    private boolean hasPermissions(){
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNewLocationData(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0); //todo maybe more? like 5000?
        locationRequest.setFastestInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
        }

    public LocationInfo getLocationInfo(){
        return locationInfo;
    }

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

        if(!gps_enabled && !network_enabled) {
            Intent noLocationIntent = new Intent();
            noLocationIntent.setAction("no_location");
            context.sendBroadcast(noLocationIntent);
            return false;
        }
        else{
            return true;
        }
    }
}

package com.idoporat.honeyimhome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationResult;


public class LocationTracker {

    private Context context;
    private BroadcastReceiver br;
    private boolean locationEnabled;

    private final static String TAG = "lack of permission";
    private final static String PERMISSION_ERROR_MSG = "app dose'nt have location permission";


    LocationTracker(Context context){
        this.context = context;
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(LocationResult.hasResult(intent)){}
                Intent location = new Intent();
                intent.setAction("location_update");
//                intent.putExtra()

            }
        };
    }

    public boolean startTracking(){
        // todo - will start tracking the location and send a "started" broadcast intent

        boolean hasPermission = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        if(hasPermission) {
            if(isLocationEnabled()){
               return true;
            }
        }
        else {
            Log.d(TAG, PERMISSION_ERROR_MSG);
        }
        return false;

        // todo - The LocationTracker class should assume it has anything it needs in order to run.
        //  but just to be on the safe side, in the LocationTracker's "startTracking()" method,
        //  add a basic check that assert you have the runtime location permission. If yes continue
        //  to track location, if not just log some error to logcat and don't do anything (optional
        //  -send an "error" broadcast and listen to it in the activity would and show some error in
        //  the UI).
    }

    public void stopTracking(){
        //todo - will stop tracking and send a "stopped" broadcast intent
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
            // notify user
//            new AlertDialog.Builder(context)
//                    .setMessage(R.string.gps_network_not_enabled)
//                    .setPositiveButton(R.string.open_location_settings,
//                                        new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//                    })
//                            .setNegativeButton(R.string.Cancel,null)
//                            .show();
            CharSequence text = "make sure location and network are enabled";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }
        else{
            return true;
        }
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(new LocationRequest()
//                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                        .setInterval(0)); //todo???







    }
}

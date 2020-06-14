package com.idoporat.honeyimhome;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;


public class LocationWorker extends ListenableWorker {

    public static final String PREVIOUS = "previous";
    private LocationTracker locationTracker;
    private Context context;
    private CallbackToFutureAdapter.Completer<Result> callback = null;
    private SharedPreferences sp;
    private Gson gson;
    private BroadcastReceiver receiver;
    private String phone = null;
    private String homeString = null;


    private static final String HOME = "homeString";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String LOCATION = "location";
    private static final String NEW_LOCATION_2 = "new_location_2";
    private static final String PHONE = "phone";
    private static final String CONTENT = "content";
    private static final String POST_PC_ACTION_SEND_SMS = "sms send action";
    private static final String STOPPED_2 = "stopped_2";
    private final static int APPLICATION = 0;


    public LocationWorker(Context context, WorkerParameters w){
        super(context, w);
        this.context = context;
        locationTracker = new LocationTracker(this.context, APPLICATION);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        gson = new Gson();
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        ListenableFuture<Result> future =
                CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<Result>(){
            @Nullable
            @Override
            public Object attachCompleter(@NonNull CallbackToFutureAdapter.Completer<Result>
                                                                    completer) throws Exception {
                callback = completer;
                return null;
            }
        });

        if(hasPermissions()){
            homeString = sp.getString(HOME, null);
            phone = sp.getString(PHONE_NUMBER, null);
            if(homeString != null && phone != null){
                placeReceiver();
                locationTracker.startTracking();
            }
        }
        else{
            if(callback != null){
                callback.set(Result.success());
            }
        }
        return future;
    }

    private void placeReceiver() {
        receiver = new LocationWorkerReceiver();
        IntentFilter filter = new IntentFilter(NEW_LOCATION_2);
        getApplicationContext().registerReceiver(receiver, filter);
    }

    private void sendSMS(Context context) {
        Intent smsIntent = new Intent(POST_PC_ACTION_SEND_SMS);
        smsIntent.putExtra(PHONE, phone);
        smsIntent.putExtra(CONTENT, context.getString(R.string.home_message));
        LocalBroadcastManager.getInstance(getApplicationContext())
                             .sendBroadcast(smsIntent);
    }

    private class LocationWorkerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String last = sp.getString(PREVIOUS, null);
            String action = intent.getAction();
            if (action != null) {
                if (NEW_LOCATION_2.equals(action)) {
                    Location current = intent.getParcelableExtra(LOCATION);
                    if (current != null && current.getAccuracy() < 50) {
                        locationTracker.stopTracking();
                        if(last != null) {
                            Location previous = gson.fromJson(last, Location.class);
                            if (current.distanceTo(previous) >= 50) {
                                {
                                    LocationInfo homeLocation =
                                                    gson.fromJson(homeString, LocationInfo.class);
                                    float[] results = calculateDistance(current, homeLocation);
                                    if ( results[0] < 50) {
                                        sendSMS(context);
                                    }
                                }
                            }
                        }
                    }
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(PREVIOUS, gson.toJson(current)).apply();
                    context.unregisterReceiver(receiver);
                    if(callback != null){
                        callback.set(Result.success());
                    }
                }
            }
        }
    }

    private float[] calculateDistance(Location current, LocationInfo homeLocation) {
        double hLatitude = homeLocation.getLatitude();
        double hLongitude = homeLocation.getLongitude();
        double cLatitude = current.getLatitude();
        double cLongitude = current.getLongitude();
        float[] results = new float[1];
        Location.distanceBetween(hLatitude, hLongitude, cLatitude,
                cLongitude, results);
        return results;
    }

    /**
     * Checks whether or not the app has location permissions
     * @return true if it has, false otherwise.
     */
    private boolean hasPermissions(){
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }
}

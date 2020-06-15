package com.idoporat.honeyimhome;

import android.app.Application;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class MyApp extends Application {
    public static final String POST_PC_ACTION_SEND_SMS = "sms send action";
    private static final String PREVIOUS = "previous"; //todo delete
    private LocalSendSmsBroadcastReceiver smsBR;

    @Override
    public void onCreate() {
        super.onCreate();

        ///////////////////////////////// todo delete! /////////////////////////////////////////////
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Log.e(PREVIOUS, sp.getString(PREVIOUS, "null"));
        Log.e(PREVIOUS, sp.getString("homeString", "null"));
//        Location current = new Location("");
//        current.setAltitude(31.776898);
//        current.setLongitude(35.190955);
//
//        SharedPreferences.Editor edit = sp.edit(); //todo delete
//        edit.putString(PREVIOUS, gson.toJson(current)).apply(); //todo delete

        ////////////////////////////////////////////////////////////////////////////////////////////

        smsBR = new LocalSendSmsBroadcastReceiver();
        IntentFilter filter = new IntentFilter(POST_PC_ACTION_SEND_SMS);
        LocalBroadcastManager.getInstance(this).registerReceiver(smsBR, filter);

        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest
                        .Builder(LocationWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        Log.e("go worker", "app about to call worker");//todo delete

        final Operation track_location = WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "track_location", ExistingPeriodicWorkPolicy.REPLACE, workRequest);



//        track_location.getState();

        Log.e("go worker", "app called worker " + track_location.getResult().toString());//todo delete
    }
}

package com.idoporat.honeyimhome;

import android.app.Application;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MyApp extends Application {
    public static final String POST_PC_ACTION_SEND_SMS = "sms send action";
    private LocalSendSmsBroadcastReceiver smsBR;

    @Override
    public void onCreate() {
        super.onCreate();

        smsBR = new LocalSendSmsBroadcastReceiver();
        IntentFilter filter = new IntentFilter(POST_PC_ACTION_SEND_SMS);
        LocalBroadcastManager.getInstance(this).registerReceiver(smsBR, filter); // todo - where to unregister?

        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(LocationWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this)
                .enqueue(saveRequest);
    }

}

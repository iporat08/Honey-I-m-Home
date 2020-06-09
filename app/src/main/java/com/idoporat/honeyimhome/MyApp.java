package com.idoporat.honeyimhome;

import android.app.Application;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyApp extends Application {
    public static final String POST_PC_ACTION_SEND_SMS = "sms send action";
    private LocalSendSmsBroadcastReceiver smsBR;

    @Override
    public void onCreate() {
        super.onCreate();

        smsBR = new LocalSendSmsBroadcastReceiver();
        IntentFilter filter = new IntentFilter(POST_PC_ACTION_SEND_SMS);
        LocalBroadcastManager.getInstance(this).registerReceiver(smsBR, filter); // todo - where to unregister?
    }

}

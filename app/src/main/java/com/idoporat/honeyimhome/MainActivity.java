package com.idoporat.honeyimhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LocationTracker locationTracker;
    private BroadcastReceiver br;
    private final int REQUEST_CODE_PERMISSION_LOCATION = 1;
    private Button trackingButton;
    private boolean tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracking = false;
        trackingButton = findViewById(R.id.Start_tracking_button);

        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
                if(hasPermission){
                    if(tracking){
                        tracking = false;
                        locationTracker.stopTracking();
                        trackingButton.setText(getString(R.string.start_tracking));
                        //todo
                    }
                    else {
                        if(locationTracker.startTracking()){
                            tracking = true;
                            trackingButton.setText(getString(R.string.stop_tracking));
                        }
                        //todo
                    }
                }
                else{
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_PERMISSION_LOCATION);
                }
            }
        });

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //todo
            }
        };
        locationTracker = new LocationTracker(this);
        //todo create filter
        // todo - register a BroadcastReceiver to listen to the LocationTracker different broadcasts
        //  , and operate on the UI accordingly.

        //todo - check and ask for permissions + handle result
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                trackingButton.setText(getString(R.string.stop_tracking));
                locationTracker.startTracking();
            }
            else{ //todo - shouldShowRequestPermissionRational?
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.rational);
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //todo - remove the br registration in onDestroy()
    }
}



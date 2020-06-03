package com.idoporat.honeyimhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LocationTracker locationTracker;
//    private LocationInfo locationInfo;
    private BroadcastReceiver br;
    private final int REQUEST_CODE_PERMISSION_LOCATION = 1;
    private Button trackingButton;
    private TextView latitudeView;
    private TextView longitudeView;
    private TextView accuracyView;
    private boolean tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracking = false;
        trackingButton = findViewById(R.id.Start_tracking_button);
        latitudeView = findViewById(R.id.latitude_textView);
        longitudeView = findViewById(R.id.longitude_textView);
        accuracyView = findViewById(R.id.accuracy_textView);
        locationTracker = new LocationTracker(this);

        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermissions()){
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
                String action = intent.getAction();
                if(action != null){
                    if(action.equals(getString(R.string.new_location))){
                        Location location = intent.getParcelableExtra(getString(R.string
                                .location_intent_key));
                        if(location != null){
                            updateLocationView();
                        }
                    }
                    else if(action.equals("no_location")){
                        new AlertDialog.Builder(context)
                                .setMessage(R.string.gps_network_not_enabled)
                                .setPositiveButton(R.string.open_location_settings,
                                                    new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        MainActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                                        .setNegativeButton(R.string.Cancel,null)
                                        .show();
                    }
                }


            }
        };
        IntentFilter filter = new IntentFilter(getString(R.string.new_location));
        filter.addAction("no_location");
        this.registerReceiver(br, filter);
    }

    private boolean hasPermissions(){
        return ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateLocationView(){
        LocationInfo location = locationTracker.getLocationInfo();
        if(location != null){
            double latitude =  locationTracker.locationInfo.getLatitude();
            double longitude = locationTracker.locationInfo.getLongitude();
            float accuracy = locationTracker.locationInfo.getAccuracy();

            latitudeView.setText(getString(R.string.latitude) + latitude);
            longitudeView.setText(getString(R.string.longitude) + longitude);
            accuracyView.setText(getString(R.string.accuracy) + accuracy);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
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

        unregisterReceiver(br);
    }
}



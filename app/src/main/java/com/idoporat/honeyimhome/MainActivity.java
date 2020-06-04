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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    ////////////////////////////////////// Constants ///////////////////////////////////////////////
    public static final String LATITUDE_TEXT = "latitudeText";
    public static final String LONGITUDE_TEXT = "longitudeText";
    public static final String ACCURACY_TEXT = "accuracyText";
    public static final String HOME_TEXT = "homeText";
    public static final String SET_HOME_BUTTON_TEXT = "setHomeButtonText";
    public static final String TRACKING_BUTTON_TEXT = "trackingButtonText";
    public static final String LATITUDE_VISIBILITY = "latitudeVisibility";
    public static final String LONGITUDE_VISIBILITY = "longitudeVisibility";
    public static final String ACCURACY_VISIBILITY = "accuracyVisibility";
    public static final String HOME_VISIBILITY = "homeVisibility";
    public static final String SET_HOME_BUTTON_VISIBILITY = "setHomeButtonVisibility";
    public static final String CLEAR_HOME_BUTTON_VISIBILITY = "clearHomeButtonVisibility";
    public static final String TRACKING_BUTTON_VISIBILITY = "trackingButtonVisibility";

    private static final String LOCATION = "location";
    static final String HOME = "homeString";
    static final String STARTED = "started";
    static final String STOPPED = "stopped";
    static final String NEW_LOCATION = "new_location";
    private static final String NO_LOCATION = "no_location";
    public static final String TRACKING = "tracking";
    private LocationTracker locationTracker;

    ////////////////////////////////////// Data Members ////////////////////////////////////////////
    private Button trackingButton;
    private Button setHomeButton;
    private Button clearHomeButton;
    private TextView latitudeView;
    private TextView longitudeView;
    private TextView accuracyView;
    private TextView homeView;
    private BroadcastReceiver br;
    private final int REQUEST_CODE_PERMISSION_LOCATION = 1;
    private boolean tracking;
    private SharedPreferences sp;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        tracking = false;
        locationTracker = new LocationTracker(this);
        setBroadcastReceiver();
        setViews();
    }

    /**
     * Sets the activity's Views and their properties.
     */
    private void setViews() {
        latitudeView = findViewById(R.id.latitude_textView);
        latitudeView.setVisibility(View.INVISIBLE);
        longitudeView = findViewById(R.id.longitude_textView);
        longitudeView.setVisibility(View.INVISIBLE);
        accuracyView = findViewById(R.id.accuracy_textView);
        accuracyView.setVisibility(View.INVISIBLE);
        homeView = findViewById(R.id.home_textView);
        homeView.setVisibility(View.INVISIBLE);

        setHomeButton = findViewById(R.id.set_home_button);
        setHomeButton.setVisibility(View.INVISIBLE);
        setHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTracker.updateHome();
                updateHomeLocationView();
            }
        });
        clearHomeButton = findViewById(R.id.clear_home_button);
        clearHomeButton.setVisibility(View.INVISIBLE);
        clearHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTracker.deleteHome();
                homeView.setVisibility(View.INVISIBLE);
                clearHomeButton.setVisibility(View.INVISIBLE);
                SharedPreferences.Editor edit = sp.edit();
                edit.remove(HOME).apply();
            }
        });
        trackingButton = findViewById(R.id.Start_tracking_button);
        trackingButton.setOnClickListener(new TrackingButtonListener());
    }

    /**
     * creates the activity's broadcast receiver and it's filter and registers them.
     */
    private void setBroadcastReceiver() {
        br = new myBroadcastReceiver();
        IntentFilter filter = new IntentFilter(NEW_LOCATION);
        filter.addAction(NO_LOCATION);
        filter.addAction(STARTED);
        filter.addAction(STOPPED);
        this.registerReceiver(br, filter);
    }

    /**
     * the onClickListener of trackingButton
     */
    private class TrackingButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(hasPermissions()){
                if(tracking){
                    locationTracker.stopTracking();
                }
                else {
                    locationTracker.startTracking();
                }
            }
            else{
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_PERMISSION_LOCATION);
            }
        }
    }

    /**
     * a BroadcastReceiver
     */
    private class myBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null){
                switch (action) {
                    case NEW_LOCATION:
                        Location location = intent.getParcelableExtra(LOCATION);
                        if (location != null) {
                            updateLocationView();
                        }
                        break;
                    case NO_LOCATION:
                        noLocationDialog(context);
                        break;
                    case STARTED:
                        tracking = true;
                        trackingButton.setText(getString(R.string.stop_tracking));
                        updateLocationView();
                        break;
                    case STOPPED:
                        tracking = false;
                        trackingButton.setText(getString(R.string.start_tracking));
                        latitudeView.setVisibility(View.INVISIBLE);
                        longitudeView.setVisibility(View.INVISIBLE);
                        accuracyView.setVisibility(View.INVISIBLE);
                        setHomeButton.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        }

        /**
         * Shows a dialog that says the location service is unavailable
         * @param context a context
         */
        private void noLocationDialog(Context context) {
            new AlertDialog.Builder(context).setMessage(R.string.gps_network_not_enabled)
                .setPositiveButton(R.string.open_location_settings,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paraDialogInterface, int paramInt) {
                                MainActivity.this.startActivity(new Intent(Settings
                                        .ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }).setNegativeButton(R.string.Cancel, null).show();
        }
    }

    /**
     * Checks whether or not the app has location permissions
     * @return true if it has, false otherwise.
     */
    private boolean hasPermissions(){
        return ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Updates the UI regarding the current location according to locationTracker
     */
    private void updateLocationView(){
        LocationInfo location = locationTracker.getLocationInfo();
        if(location != null){
            latitudeView.setText(getString(R.string.latitude) + ' ' + location.getLatitude());
            longitudeView.setText(getString(R.string.longitude) + ' ' + location.getLongitude());
            accuracyView.setText(getString(R.string.accuracy) + ' ' + location.getAccuracy());
            latitudeView.setVisibility(View.VISIBLE);
            longitudeView.setVisibility(View.VISIBLE);
            accuracyView.setVisibility(View.VISIBLE);
            if(location.getAccuracy() < 50){
                setHomeButton.setVisibility(View.VISIBLE);
            }
            else{
                setHomeButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Updates the UI regarding the home location according to locationTracker
     */
    private void updateHomeLocationView(){
        LocationInfo home = locationTracker.getHomeLocation();
        double latitude = home.getLatitude();
        double longitude = home.getLongitude();
        String newHomeLocation = getString(R.string.home, latitude, longitude);
        homeView.setText(newHomeLocation);
        homeView.setVisibility(View.VISIBLE);
        clearHomeButton.setVisibility(View.VISIBLE);
        String homeString = gson.toJson(home);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(HOME,homeString).apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
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
        if(tracking){
            locationTracker.stopTracking();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String homeString = sp.getString(HOME, null);
        if(homeString != null){
            locationTracker.updateHome(gson.fromJson(homeString, LocationInfo.class));
            updateHomeLocationView();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(TRACKING, tracking);

        outState.putString(LATITUDE_TEXT, latitudeView.getText().toString());
        outState.putString(LONGITUDE_TEXT, longitudeView.getText().toString());
        outState.putString(ACCURACY_TEXT, accuracyView.getText().toString());
        outState.putString(HOME_TEXT, homeView.getText().toString());
        outState.putString(SET_HOME_BUTTON_TEXT, setHomeButton.getText().toString());
        outState.putString(TRACKING_BUTTON_TEXT, trackingButton.getText().toString());

        outState.putInt(LATITUDE_VISIBILITY, latitudeView.getVisibility());
        outState.putInt(LONGITUDE_VISIBILITY, longitudeView.getVisibility());
        outState.putInt(ACCURACY_VISIBILITY, accuracyView.getVisibility());
        outState.putInt(HOME_VISIBILITY, homeView.getVisibility());
        outState.putInt(SET_HOME_BUTTON_VISIBILITY, setHomeButton.getVisibility());
        outState.putInt(CLEAR_HOME_BUTTON_VISIBILITY, clearHomeButton.getVisibility());
        outState.putInt(TRACKING_BUTTON_VISIBILITY, trackingButton.getVisibility());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tracking = savedInstanceState.getBoolean(TRACKING);
        if(tracking){
            locationTracker.startTracking();
        }

        latitudeView.setText(savedInstanceState.getString(LATITUDE_TEXT));
        longitudeView.setText(savedInstanceState.getString(LONGITUDE_TEXT));
        accuracyView.setText(savedInstanceState.getString(ACCURACY_TEXT));
        homeView.setText(savedInstanceState.getString(HOME_TEXT));
        setHomeButton.setText(savedInstanceState.getString(SET_HOME_BUTTON_TEXT));
        trackingButton.setText(savedInstanceState.getString(TRACKING_BUTTON_TEXT));

        latitudeView.setVisibility(savedInstanceState.getInt(LATITUDE_VISIBILITY));
        longitudeView.setVisibility(savedInstanceState.getInt(LONGITUDE_VISIBILITY));
        accuracyView.setVisibility(savedInstanceState.getInt(ACCURACY_VISIBILITY));
        homeView.setVisibility(savedInstanceState.getInt(HOME_VISIBILITY));
        setHomeButton.setVisibility(savedInstanceState.getInt(SET_HOME_BUTTON_VISIBILITY));
        clearHomeButton.setVisibility(savedInstanceState.getInt(CLEAR_HOME_BUTTON_VISIBILITY));
        trackingButton.setVisibility(savedInstanceState.getInt(TRACKING_BUTTON_VISIBILITY));
    }
}



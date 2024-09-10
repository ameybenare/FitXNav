package com.example.gpsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PERMISSION_FINE_LOCATION = 92;
    FirebaseAuth auth;
    TextView welcome;

    //TextView tvLongitude,tvLatitude, tvAltitude,tvAccuracy,tvSpeed,tvAddress;

    TextView tvLongitudeVal, tvLatitudeVal, tvAltitudeVal, tvAccuracyVal, tvSpeedVal, tvAddressVal, tvUpdates, tvSensor, tvWayPointCounts;
    Switch swGps, swLocationUpdates;
    Button btnNewWayPoint, btnShowWayPointList,btnShowMap;
    //Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    //LocationRequest is a config file for above class
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    //current location
    Location currentLocation;
    //list of saved location
    List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLongitudeVal = findViewById(R.id.tvLongitudeVal);
        tvLatitudeVal = findViewById(R.id.tvLatitudeVal);
        tvAltitudeVal = findViewById(R.id.tvAltitudeVal);
        tvAccuracyVal = findViewById(R.id.tvAccuracyVal);
        tvSpeedVal = findViewById(R.id.tvSpeedVal);
        tvAddressVal = findViewById(R.id.tvAddressVal);
        tvUpdates = findViewById(R.id.tvUpdates);
        tvSensor = findViewById(R.id.tvSensor);
        swGps = findViewById(R.id.sw_gps);
        swLocationUpdates = findViewById(R.id.sw_locationsupdates);
        btnShowMap = findViewById(R.id.btnShowMap);
        btnNewWayPoint = findViewById(R.id.btnNewWayPoint);
        btnShowWayPointList = findViewById(R.id.btnShowWayPointList);
        tvWayPointCounts = findViewById(R.id.tvCountOfCrumbs);

        setLocationRequestProp();
        //method is triggered when the update interval for location is meet
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                updateUIValues(locationResult.getLastLocation());
            }
        };
        btnNewWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get grp location

                //add the new location to the global list
                //MyApplication myApplication = (MyApplication)getApplicationContext();
                //savedLocations = myApplication.getMyLocations();
                //  savedLocations.add(currentLocation);
            }
        });

        btnShowWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ShowSavedLocationsList.class);
                startActivity(i);
            }
        });

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });
        swGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swGps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tvSensor.setText("Using GPS Sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tvSensor.setText("Using Wifi + cell tower");
                }
            }
        });

        swLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swLocationUpdates.isChecked()) {
                    //turn on tracking
                    startLocationUpdates();
                } else {
                    //turn off tracking
                    stopLocationUpdates();
                }
            }
        });
        auth = FirebaseAuth.getInstance();
        welcome = findViewById(R.id.welcome);
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        welcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        updateGPS();
    }//End of onCreate method

    private void startLocationUpdates() {
        tvUpdates.setText("Location is  tracked");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    private void stopLocationUpdates() {
        tvUpdates.setText("Location is not being tracked");
        tvLongitudeVal.setText("Location not tracked");
        tvLatitudeVal.setText("Location not tracked");
        tvSpeedVal.setText("Location not tracked");
        tvAddressVal.setText("Location not tracked");
        tvAccuracyVal.setText("Location not tracked");
        tvAltitudeVal.setText("Location not tracked");
        tvSensor.setText("Location not tracked");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(this, "App requires permission to be granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS(){
        //get permission and get current location and update UI
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            //user provided permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    updateUIValues(location);
                    currentLocation = location;
                    MyApplication myApplication = (MyApplication)getApplicationContext();
                    if(myApplication.getStartLocation()==null){
                        myApplication.setStartLocation(location);
                    }
                    savedLocations = myApplication.getMyLocations();
                    savedLocations.add(currentLocation);
                }

            });
        }else{
            //permission not granted
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//Andriod os should be 23 0r higer
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);

            }
        }
    }

    private void updateUIValues(Location location) {
        if(location != null){
            tvLatitudeVal.setText(String.valueOf(location.getLatitude()));
            tvLongitudeVal.setText(String.valueOf(location.getLongitude()));
            tvAccuracyVal.setText(String.valueOf(location.getAccuracy()));

            if(location.hasAltitude()){
                tvAltitudeVal.setText(String.valueOf(location.getAltitude()));
            }else{
                tvAltitudeVal.setText("Not Available");
            }


            if(location.hasSpeed()){
                tvSpeedVal.setText(String.valueOf(location.getSpeed()));
            }else{
                tvSpeedVal.setText("Not Available");
            }

            Geocoder geoCoder = new Geocoder(MainActivity.this);
            try{
                List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                tvAddressVal.setText(addresses.get(0).getAddressLine(0));
            }catch(Exception e){
                tvAddressVal.setText("Address not available");
            }

            MyApplication myApplication = (MyApplication)getApplicationContext();
            savedLocations = myApplication.getMyLocations();
            //Show the no. of waypoint saved
            tvWayPointCounts.setText(Integer.toString(savedLocations.size()));

        }


    }

    //Sets all the properties for the LocationRequest Class
    private void setLocationRequestProp(){
        System.out.println("Entereeddddddddd");
        locationRequest = new LocationRequest();
        //how often location check must occur?
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        //how often location check must occur when set to most frequent update?
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
}
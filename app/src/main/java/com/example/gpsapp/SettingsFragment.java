package com.example.gpsapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MyApplication myApplication = null;
    Geocoder geoCoder;
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PERMISSION_FINE_LOCATION = 92;
    FirebaseAuth auth;
    TextView welcome;
    ViewModelProvider viewModelProvider ;

    // Obtain the ViewModel
    LocationViewModel locationViewModel ;

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
    static List<Location> savedLocations;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

//        args.putParcelableArrayList("locationList", (ArrayList<Location>) savedLocations);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        tvLongitudeVal = view.findViewById(R.id.tvLongitudeVal);
        tvLatitudeVal = view.findViewById(R.id.tvLatitudeVal);
        tvAltitudeVal = view.findViewById(R.id.tvAltitudeVal);
        tvAccuracyVal = view.findViewById(R.id.tvAccuracyVal);
        tvSpeedVal = view.findViewById(R.id.tvSpeedVal);
        tvAddressVal = view.findViewById(R.id.tvAddressVal);
        tvUpdates = view.findViewById(R.id.tvUpdates);
        tvSensor = view.findViewById(R.id.tvSensor);
        swGps = view.findViewById(R.id.sw_gps);
        swLocationUpdates = view.findViewById(R.id.sw_locationsupdates);
//        btnShowMap = view.findViewById(R.id.btnShowMap);
        btnNewWayPoint = view.findViewById(R.id.btnNewWayPoint);
        btnShowWayPointList = view.findViewById(R.id.btnShowWayPointList);
        tvWayPointCounts = view.findViewById(R.id.tvCountOfCrumbs);

        setLocationRequestProp();
        //method is triggered when the update interval for location is meet
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                updateUIValues(locationResult.getLastLocation());
               // updateGPS();
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
//        btnShowMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getActivity(),MapsActivity.class);
//                startActivity(i);
//            }
//        });
        btnShowWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),ShowSavedLocationsList.class);
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
        updateGPS();
        if(swLocationUpdates.isChecked()){
            startLocationUpdates();
        }
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
        //updateGPS();
        return view;
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
                    Toast.makeText(this.getContext(), "App requires permission to be granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startLocationUpdates() {
        tvUpdates.setText("Location is  tracked");

        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void updateGPS(){
        //get permission and get current location and update UI
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        if(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            //user provided permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                    currentLocation = location;

                  //  myApplication = (MyApplication)getActivity().getApplicationContext();
                    if(myApplication.getStartLocation()==null){
                        myApplication.setStartLocation(location);
                    }
                    savedLocations = myApplication.getMyLocations();
                    if(savedLocations.size()==0){
                        savedLocations.add(currentLocation);
                    }else{
                        if(savedLocations.get(savedLocations.size()-1).getLongitude()!=currentLocation.getLongitude() &&
                                savedLocations.get(savedLocations.size()-1).getLatitude()!=currentLocation.getLatitude()   ) {
                            savedLocations.add(currentLocation);
                            // Initialize the ViewModelProvider with the fragment's ViewModelStoreOwner
                      //      ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
                            // Obtain the ViewModel
                        //    LocationViewModel locationViewModel = viewModelProvider.get(LocationViewModel.class);
                            locationViewModel.setNewLocation(currentLocation);
                        }
                    }
                    /*if(savedLocations!=null &&  savedLocations.size()>1  && savedLocations.get(savedLocations.size()-1) != currentLocation){
                        savedLocations.add(currentLocation);
                    }*/


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

            // geoCoder = new Geocoder(requireContext());
            try{
                List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                tvAddressVal.setText(addresses.get(0).getAddressLine(0));
            }catch(Exception e){
                tvAddressVal.setText("Address not available");
            }
            currentLocation = location;
            savedLocations = myApplication.getMyLocations();
            if(savedLocations.size()==0){
                savedLocations.add(currentLocation);
            }else{
                if(savedLocations.get(savedLocations.size()-1).getLongitude()!=currentLocation.getLongitude() &&
                        savedLocations.get(savedLocations.size()-1).getLatitude()!=currentLocation.getLatitude()   ) {
                    savedLocations.add(currentLocation);
                    // Initialize the ViewModelProvider with the fragment's ViewModelStoreOwner
                  //  ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
                    // Obtain the ViewModel
                    //LocationViewModel locationViewModel = viewModelProvider.get(LocationViewModel.class);
                    locationViewModel.setNewLocation(currentLocation);
                }
            }
          /*  if(savedLocations!=null && savedLocations.size()>1 && savedLocations.get(savedLocations.size()-1) != currentLocation){
                savedLocations.add(location);
            }*/

            //Show the no. of waypoint saved
            tvWayPointCounts.setText(""+savedLocations.size());

        }

    }

    public Context getApplicationContext() {
        return this.getActivity() == null ? (getActivity() == null ? null : getActivity()
                .getApplicationContext()) : this.getActivity().getApplicationContext();
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
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        geoCoder = new Geocoder(context);
        myApplication = (MyApplication)getActivity().getApplicationContext();
        // Now you can safely use the 'context' object.
    }

   /* @Override
    public void onLocationChanged(@NonNull Location location) {
        System.out.println("In location chnaged method");
        if (location != null) {
           updateGPS();
            // You can also update the UI or perform any other actions here
        }
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize the ViewModelProvider with the fragment's ViewModelStoreOwner
         viewModelProvider = new ViewModelProvider(requireActivity());

        // Obtain the ViewModel
         locationViewModel = viewModelProvider.get(LocationViewModel.class);

        // Now you can safely use locationViewModel in this fragment
    }

}


package com.example.gpsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gpsapp.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FusedLocationProviderClient fusedLocationProviderClient;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ViewModelProvider viewModelProvider ;


    LocationViewModel locationViewModel ;

    MyApplication myApplication = null;
    List<Location> savedLocations;
    private GoogleMap mMap;
    PolylineOptions polylineOptions;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        if(getArguments().getParcelableArrayList("locationList") != null) {
//            savedLocations = getArguments().getParcelableArrayList("locationList");
//        }
        myApplication = (MyApplication) getActivity().getApplicationContext();
        savedLocations = myApplication.getMyLocations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //   mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng lastLocationPlaced = sydney;
        List<LatLng> listLatLng = new ArrayList<LatLng>();
        MyApplication myApplication = (MyApplication) getActivity().getApplicationContext();
        Location startLocation = myApplication.getStartLocation();
        if(startLocation != null){
            lastLocationPlaced = new LatLng(startLocation.getLatitude(),startLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(new LatLng(startLocation.getLatitude(),startLocation.getLongitude())));
            for(Location location : savedLocations){
                if(location != null){
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
               /* MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Lat:"+location.getLatitude()+" Lon:"+location.getLongitude());
                mMap.addMarker(markerOptions);
                lastLocationPlaced = latLng;*/
                    listLatLng.add(latLng);

                    //mMap.addPolyline(new PolylineOptions().clickable(true).add(latLng));
                }

            }
        }

         polylineOptions = new PolylineOptions();
        polylineOptions=polylineOptions.color(Color.RED);
        //    polylineOptions.addSpan(new StyleSpan(0,2));
        polylineOptions=  polylineOptions.addAll(listLatLng);
        polylineOptions=  polylineOptions.clickable(true);
        polylineOptions=polylineOptions.width(6);
        mMap.addPolyline(polylineOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced,12.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                //counts number of times marker is clicked

                Integer clicks = (Integer) marker.getTag();
                if(clicks == null){
                    clicks = 0;
                }
                clicks++;
                marker.setTag(clicks);
                Toast.makeText(getActivity(), "Marker "+marker.getTitle()+ " was clicked "+ marker.getTag()+" times.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Initialize the ViewModelProvider with the fragment's ViewModelStoreOwner
        // viewModelProvider = new ViewModelProvider(requireActivity());
        // Obtain the ViewModel
        //LocationViewModel locationViewModel = viewModelProvider.get(LocationViewModel.class);
        locationViewModel.getNewLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                LatLng newLoc = new LatLng(location.getLatitude(),location.getLongitude());
                polylineOptions = polylineOptions.add(newLoc);
                mMap.addPolyline(polylineOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLoc, 12.0f));
            }
        });
    }

    /*@Override
    public void onLocationChanged(@NonNull Location location) {
    polylineOptions = polylineOptions.add(new LatLng(location.getLatitude(),location.getLongitude()));
    }*/
/*
    private void startLocationUpdates() {
       // tvUpdates.setText("Location is  tracked");

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
*/

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
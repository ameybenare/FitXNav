package com.example.gpsapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gpsapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StyleSpan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    List<Location> savedLocations;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MyApplication myApplication = (MyApplication) getApplicationContext();
        savedLocations = myApplication.getMyLocations();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //   mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng lastLocationPlaced = sydney;
        List<LatLng> listLatLng = new ArrayList<LatLng>();
        MyApplication myApplication = (MyApplication) getApplicationContext();
        Location startLocation = myApplication.getStartLocation();
        lastLocationPlaced = new LatLng(startLocation.getLatitude(),startLocation.getLongitude());

        mMap.addMarker(new MarkerOptions().position(new LatLng(startLocation.getLatitude(),startLocation.getLongitude())));
        for(Location location : savedLocations){
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
           /* MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Lat:"+location.getLatitude()+" Lon:"+location.getLongitude());
            mMap.addMarker(markerOptions);
            lastLocationPlaced = latLng;*/
            listLatLng.add(latLng);

            //mMap.addPolyline(new PolylineOptions().clickable(true).add(latLng));
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions=polylineOptions.color(Color.GREEN);
        //    polylineOptions.addSpan(new StyleSpan(0,2));
        polylineOptions=  polylineOptions.addAll(listLatLng);
        polylineOptions=  polylineOptions.clickable(true);
        polylineOptions=polylineOptions.width(5);
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
                Toast.makeText(MapsActivity.this, "Marker "+marker.getTitle()+ " was clicked "+ marker.getTag()+" times.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
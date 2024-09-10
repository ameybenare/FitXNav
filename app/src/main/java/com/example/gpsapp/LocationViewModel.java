package com.example.gpsapp;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {
    private MutableLiveData<Location> newLocation = new MutableLiveData<>();

    public void setNewLocation(Location location) {
        newLocation.setValue(location);
    }

    public LiveData<Location> getNewLocation() {
        return newLocation;
    }
}

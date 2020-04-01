package com.jamdoli.corus.gps;

import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class GpsHandler {

    private final FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;

    public GpsHandler(FusedLocationProviderClient fusedLocationClient) {
        this.fusedLocationClient = fusedLocationClient;

        lastLocation = new Location("");
        initiateLocationFetching();
    }


    public Location getLastLocation() {
        initiateLocationFetching();

        return lastLocation;
    }

    private void initiateLocationFetching() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lastLocation = location;
                        }
                    }
                });
    }
}

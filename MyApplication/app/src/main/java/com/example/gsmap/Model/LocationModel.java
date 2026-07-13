package com.example.gsmap.Model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class LocationModel {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public interface LocationListener {
        void onLocationChanged(double lat, double lon);
    }
    public void stop() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public void start(Context context, LocationListener listener) {

        //二重登録防止：すでに動いていたら一旦停止してから登録しなおす
        stop();

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

        //権限確認
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result == null) return;

                for (android.location.Location location : result.getLocations()) {

                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    listener.onLocationChanged(lat, lon);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                context.getMainLooper()
        );
    }
}

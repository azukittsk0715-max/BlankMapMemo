package com.example.gsmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 100;

    private MapView mapView;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // osmdroid設定
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map);

        // 国土地理院タイル
        XYTileSource tileSource = new XYTileSource(
                "GSI",
                5,
                18,
                256,
                ".png",
                new String[]{
                        "https://cyberjapandata.gsi.go.jp/xyz/std/"
                }
        );

        mapView.setTileSource(tileSource);

        mapView.setMultiTouchControls(true);

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        // 権限確認
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_PERMISSION
            );

        } else {

            getCurrentLocation();
        }
    }

    // 現在位置取得
    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        // 地図中心設定
                        GeoPoint point = new GeoPoint(lat, lon);

                        mapView.getController().setZoom(15.0);
                        mapView.getController().setCenter(point);

                        // マーカー追加
                        Marker marker = new Marker(mapView);

                        marker.setPosition(point);
                        marker.setTitle("現在地");

                        mapView.getOverlays().add(marker);

                    }
                });
    }

    // 権限許可後
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_PERMISSION) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                getCurrentLocation();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
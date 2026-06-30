package com.example.gsmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.gsmap.Controller.SecondActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 100;

    private MapView mapView;
    private MapViewController mapController;
    private LocationModel locationModel;
    //private SaveLocationModel saveLocationModel;

    private boolean isGpsOn = false;
    private Button gpsButton;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map);

        // ✅ 地図Controller初期化
        mapController = new MapViewController(mapView);
        mapController.initMap();

        // ✅ 位置Model初期化
        locationModel = new LocationModel();

        // ✅ 権限チェック
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
            startLocation();
        }

        //GPS-ON/OFFスイッチ
        gpsButton = findViewById(R.id.gpsButton);

        gpsButton.setOnClickListener(v -> {

            isGpsOn = !isGpsOn;

            if (isGpsOn) {
                gpsButton.setText("GPS ON");

                // ✅ GPS開始
                startLocation();

            } else {
                gpsButton.setText("GPS OFF");

                // ✅ GPS停止
                locationModel.stop();
            }
        });

        //画面遷移
        button = findViewById(R.id.frameButton);
        button.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SecondActivity.class
            );

            startActivity(intent);
        });
    }

    // ✅ LocationModel開始
    private void startLocation() {
        locationModel.start(this, (lat, lon) -> {
            mapController.updateLocation(lat, lon);
            /*
            String time = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
            ).format(new Date());

            saveLocationModel.save(lat,lon,time)*/
        });
    }

    // ✅ 権限結果
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

                startLocation();
            }
        }
    }
}
package com.example.gsmap.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;


import com.example.gsmap.Model.SaveLocationModel;
import com.example.gsmap.R;
import com.example.gsmap.View.MapViewController;
import com.example.gsmap.Model.LocationModel;

import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 100;

    private MapView mapView;
    private MapViewController mapController;
    private LocationModel locationModel;

    private SaveLocationModel savelocationModel;

    private com.example.gsmap.Model.RouteModel routeModel;
    private String currentWalkerId;

    private boolean isGpsOn = false;
    private Button gpsButton;
    private Button button;

    private String walkerId = "test";
    LocalDateTime now = LocalDateTime.now();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ログイン画面から渡されたwalker_idを受け取る
        currentWalkerId = getIntent().getStringExtra("walker_id");
        if (currentWalkerId == null) {
            currentWalkerId = "enomoto"; // 直接MainActivityを開いた場合の保険（テスト用）
        }

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map);

        // ✅ 地図Controller初期化
        mapController = new MapViewController(
                this,
                mapView,
                walkerId
        );
        mapController.initMap();

        // ✅ 位置Model初期化
        locationModel = new LocationModel();

        routeModel = new com.example.gsmap.Model.RouteModel();
        // ログイン後：過去の移動経路を取得して霧を晴らし直す
        new Thread(() -> {
            java.util.List<com.example.gsmap.Model.RouteModel.RoutePoint> past =
                    routeModel.getRoutes(currentWalkerId);
            runOnUiThread(() -> {
                for (com.example.gsmap.Model.RouteModel.RoutePoint p : past) {
                    mapController.addVisitedArea(p.latitude, p.longitude);
                }
            });
        }).start();

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

            // 取得した位置をサーバーに保存（通信は別スレッドで）
            new Thread(() -> routeModel.saveRoute(currentWalkerId, lat, lon)).start();

            Timestamp cTime = new Timestamp(System.currentTimeMillis());

            new Thread(() -> {
                SaveLocationModel saveLocationModel = new SaveLocationModel();
                boolean result = saveLocationModel.saveRoutePoint(walkerId, lat, lon, cTime);
            }).start();

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

package com.example.gsmap.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.gsmap.Model.LocationModel;
import com.example.gsmap.Model.RouteModel;
import com.example.gsmap.Model.SaveLocationModel;
import com.example.gsmap.Model.PinModel.GetPinInfo;
import com.example.gsmap.R;
import com.example.gsmap.View.MapViewController;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.gsmap.Model.PinModel.PinAddActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 100;

    private MapView mapView;
    private MapViewController mapController;
    private LocationModel locationModel;

    private RouteModel routeModel;

    private String currentWalkerId;

    private boolean isGpsOn = false;
    private boolean isLocationPermissionGranted = false;

    private Button gpsButton;
    private Button button;

    LocalDateTime now = LocalDateTime.now();
    private final ActivityResultLauncher<Intent>
            pinAddLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            Intent data =
                                    result.getData();

                            double lat =
                                    data.getDoubleExtra(
                                            "LATITUDE",
                                            0
                                    );

                            double lon =
                                    data.getDoubleExtra(
                                            "LONGITUDE",
                                            0
                                    );

                            String memo =
                                    data.getStringExtra(
                                            "MEMO"
                                    );

                            mapController.addPin(
                                    lat,
                                    lon,
                                    memo
                            );
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // ログイン画面から渡されたwalker_idを取得
        currentWalkerId =
                getIntent().getStringExtra("walker_id");

        if (currentWalkerId == null) {
            currentWalkerId = "enomoto";
        }

        Configuration.getInstance()
                .setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map);

        // 地図初期化
        mapController = new MapViewController(
                this,
                mapView,
                currentWalkerId
        );

        mapController.initMap();

        mapController.setOnMapTapListener(
                (lat, lon) -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    PinAddActivity.class
                            );

                    intent.putExtra(
                            "LATITUDE",
                            lat
                    );

                    intent.putExtra(
                            "LONGITUDE",
                            lon
                    );

                    intent.putExtra(
                            "WALKER_ID",
                            currentWalkerId
                    );

                    pinAddLauncher.launch(
                            intent
                    );
                }
        );

        // 位置情報管理
        locationModel = new LocationModel();

        // 経路管理
        routeModel = new RouteModel();

        // 過去の移動経路表示
        new Thread(() -> {

            java.util.List<RouteModel.RoutePoint> past =
                    routeModel.getRoutes(currentWalkerId);

            runOnUiThread(() -> {

                for (RouteModel.RoutePoint p : past) {

                    mapController.addVisitedArea(
                            p.latitude,
                            p.longitude
                    );
                }
            });

        }).start();

        // 保存済みピン表示
        loadPins();

        // 権限チェック
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

            isLocationPermissionGranted = true;
        }

        // GPSボタン
        gpsButton = findViewById(R.id.gpsButton);

        gpsButton.setOnClickListener(v -> {

            if (!isLocationPermissionGranted) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_PERMISSION
                );

                return;
            }

            isGpsOn = !isGpsOn;

            if (isGpsOn) {

                gpsButton.setText("GPS ON");

                startLocation();

            } else {

                gpsButton.setText("GPS OFF");

                locationModel.stop();
            }
        });

        // 画面遷移
        button = findViewById(R.id.frameButton);

        button.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SecondActivity.class
                    );

            intent.putExtra(
                    "walker_id",
                    currentWalkerId
            );

            startActivity(intent);
        });
    }

    /**
     * GPS開始
     */
    private void startLocation() {

        locationModel.start(this, (lat, lon) -> {

            mapController.updateLocation(
                    lat,
                    lon
            );

            // RouteModelへ保存
            new Thread(() ->
                    routeModel.saveRoute(
                            currentWalkerId,
                            lat,
                            lon
                    )
            ).start();

            // API保存
            Timestamp cTime =
                    new Timestamp(
                            System.currentTimeMillis()
                    );

            new Thread(() -> {

                SaveLocationModel saveLocationModel =
                        new SaveLocationModel();

                saveLocationModel.saveRoutePoint(
                        currentWalkerId,
                        lat,
                        lon,
                        cTime
                );

            }).start();
        });
    }

    /**
     * 権限取得結果
     */
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

                isLocationPermissionGranted = true;

            } else {

                isLocationPermissionGranted = false;
            }
        }
    }

    /**
     * DBからピンを取得して表示
     */
    private void loadPins() {

        new Thread(() -> {

            try {

                GetPinInfo getPinInfo =
                        new GetPinInfo();

                JSONArray pins =
                        getPinInfo.getPins(
                                currentWalkerId
                        );

                runOnUiThread(() -> {

                    try {

                        for (int i = 0;
                             i < pins.length();
                             i++) {

                            JSONObject pin =
                                    pins.getJSONObject(i);

                            mapController.addPin(
                                    pin.getDouble("latitude"),
                                    pin.getDouble("longitude"),
                                    pin.optString("memo", "")
                            );
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {

                e.printStackTrace();
            }

        }).start();
    }
}
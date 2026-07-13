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

import com.example.gsmap.R;
import com.example.gsmap.MapViewController;
import com.example.gsmap.LocationModel;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 100;

    private MapView mapView;
    private MapViewController mapController;
    private LocationModel locationModel;

    private com.example.gsmap.Model.RouteModel routeModel;
    private String currentWalkerId;


    private boolean isGpsOn = false;
    private Button gpsButton;
    private Button button;

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
        mapController = new MapViewController(mapView);
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

        // ===== 認証APIテスト（動作確認用・あとで消す）=====
        testAuthApi();
        testAuthController();


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

        });
    }

    // ===== 認証APIテスト用メソッド（動作確認が済んだら削除）=====
    private void testAuthApi() {
        new Thread(() -> {
            com.example.gsmap.Model.WalkerModel walkerModel =
                    new com.example.gsmap.Model.WalkerModel();

            // テスト①：新規登録（apptestというIDで登録してみる）
            boolean registered = walkerModel.registerWalker("apptest", "mypass");
            android.util.Log.d("AUTH_TEST", "登録結果: " + registered);

            // テスト②：正しいパスワードでログイン（成功するはず）
            boolean loginOk = walkerModel.verifyPassword("apptest", "mypass");
            android.util.Log.d("AUTH_TEST", "ログイン(正しいPW): " + loginOk);

            // テスト③：間違ったパスワードでログイン（失敗するはず）
            boolean loginNg = walkerModel.verifyPassword("apptest", "wrongpass");
            android.util.Log.d("AUTH_TEST", "ログイン(間違いPW): " + loginNg);
        }).start();
    }

    // C2 AuthControllerのテスト
    private void testAuthController() {
        new Thread(() -> {
            AuthController authController = new AuthController();

            // 新規登録テスト
            int registerResult = authController.RegisterUser("ctrltest", "ctrlpass");
            Log.d("CTRL_TEST", "登録結果: " + registerResult);

            // 正しいPWでログインテスト
            int loginOk = authController.AuthenticateUser("ctrltest", "ctrlpass");
            Log.d("CTRL_TEST", "ログイン(正しいPW): " + loginOk);

            // 間違ったPWでログインテスト
            int loginNg = authController.AuthenticateUser("ctrltest", "wrongpass");
            Log.d("CTRL_TEST", "ログイン(間違いPW): " + loginNg);
        }).start();
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
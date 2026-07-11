package com.example.gsmap.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

    private boolean isGpsOn = false;
    private Button gpsButton;
    private Button button;

    private String walkerId = "test";
    LocalDateTime now = LocalDateTime.now();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            String cTime = now.format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );

            savelocationModel.saveRoutePoint(walkerId, lat, lon,cTime);
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

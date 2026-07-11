package com.example.gsmap.Controller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gsmap.R;
import com.example.gsmap.Model.GetLocationModel;
import com.example.gsmap.Model.RoutePoint;
import com.example.gsmap.Model.ScoreInfo;
import com.example.gsmap.Model.ScoreProcessor;

import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private TextView txtScore;
    private TextView txtDistance;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        txtScore = findViewById(R.id.txtScore);
        txtDistance = findViewById(R.id.txtDistance);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        String walkerId = getIntent().getStringExtra("walker_id");

        if (walkerId == null || walkerId.isEmpty()) {
            walkerId = "user001";
        }

        loadScore(walkerId);
    }

    private void loadScore(String walkerId) {
        txtScore.setText("スコア：取得中");
        txtDistance.setText("累計距離：取得中");

        new Thread(() -> {
            GetLocationModel getLocationModel = new GetLocationModel();
            List<RoutePoint> path = getLocationModel.fetchRouteData(walkerId);

            ScoreProcessor scoreProcessor = new ScoreProcessor();
            ScoreInfo scoreInfo = new ScoreInfo(walkerId, 250);

            double totalDistance = scoreProcessor.calculateTotalDistance(path);
            int newScore = scoreProcessor.calcScore(scoreInfo, path);

            runOnUiThread(() -> {
                txtScore.setText("スコア：" + newScore + "点");
                txtDistance.setText("累計距離：" + String.format("%.1f", totalDistance) + "m");
            });
        }).start();
    }
}
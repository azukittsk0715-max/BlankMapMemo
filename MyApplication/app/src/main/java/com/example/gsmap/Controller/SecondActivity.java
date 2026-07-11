package com.example.gsmap.Controller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gsmap.R;
import com.example.gsmap.Model.GetLocationModel;
import com.example.gsmap.Model.RoutePoint;
import com.example.gsmap.Model.ScoreApiModel;
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
            ScoreApiModel scoreApiModel = new ScoreApiModel();
            GetLocationModel getLocationModel = new GetLocationModel();
            ScoreProcessor scoreProcessor = new ScoreProcessor();

            ScoreInfo currentScoreInfo = scoreApiModel.fetchScore(walkerId);
            List<RoutePoint> path = getLocationModel.fetchRouteData(walkerId);

            double totalDistance = scoreProcessor.calculateTotalDistance(path);
            int newScore = scoreProcessor.calcScore(currentScoreInfo, path);

            ScoreInfo newScoreInfo = new ScoreInfo(walkerId, newScore);
            boolean saveResult = scoreApiModel.saveScore(newScoreInfo);

            runOnUiThread(() -> {
                if (saveResult) {
                    txtScore.setText("スコア：" + newScore + "点");
                } else {
                    txtScore.setText("スコア：" + newScore + "点（保存失敗）");
                }

                txtDistance.setText("累計距離：" + String.format("%.1f", totalDistance) + "m");
            });
        }).start();
    }
}
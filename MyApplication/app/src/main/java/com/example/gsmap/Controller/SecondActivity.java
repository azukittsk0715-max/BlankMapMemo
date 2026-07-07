package com.example.gsmap.Controller;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gsmap.R;
import com.example.gsmap.Model.RoutePoint;
import com.example.gsmap.Model.ScoreInfo;
import com.example.gsmap.Model.ScoreProcessor;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private TextView txtScore;
    private TextView txtDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        txtScore = findViewById(R.id.txtScore);
        txtDistance = findViewById(R.id.txtDistance);

        String walkerId = getIntent().getStringExtra("walker_id");

        if (walkerId == null || walkerId.isEmpty()) {
            walkerId = "user001";
        }

        ScoreProcessor scoreProcessor = new ScoreProcessor();

        ScoreInfo scoreInfo = new ScoreInfo(walkerId, 250);

        List<RoutePoint> path = new ArrayList<>();
        path.add(new RoutePoint(35.681236, 139.767125, "2026-06-16T10:00:00"));
        path.add(new RoutePoint(35.681500, 139.768000, "2026-06-16T10:00:05"));
        path.add(new RoutePoint(35.682000, 139.769000, "2026-06-16T10:00:10"));

        double totalDistance = scoreProcessor.calculateTotalDistance(path);
        int newScore = scoreProcessor.calcScore(scoreInfo, path);

        txtScore.setText("スコア：" + newScore + "点");
        txtDistance.setText("累計距離：" + String.format("%.1f", totalDistance) + "m");
    }
}
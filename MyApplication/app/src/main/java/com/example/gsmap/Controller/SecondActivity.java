package com.example.gsmap.Controller;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gsmap.Model.GetLocationModel;
import com.example.gsmap.Model.RoutePoint;
import com.example.gsmap.Model.ScoreApiModel;
import com.example.gsmap.Model.ScoreInfo;
import com.example.gsmap.Model.ScoreProcessor;
import com.example.gsmap.R;

import java.util.List;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {

    private TextView txtScore;
    private TextView txtDistance;
    private Button btnBack;

    private String currentWalkerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        txtScore = findViewById(R.id.txtScore);
        txtDistance = findViewById(R.id.txtDistance);
        btnBack = findViewById(R.id.btnBack);

        currentWalkerId =
                getIntent().getStringExtra("walker_id");

        if (currentWalkerId == null
                || currentWalkerId.isEmpty()) {

            currentWalkerId = "user001";
        }

        Log.d(
                "SCORE_PROCESS",
                "walker_id=" + currentWalkerId
        );

        btnBack.setOnClickListener(v -> finish());

        calculateAndDisplayScore();
    }

    private void calculateAndDisplayScore() {
        txtScore.setText("スコア：計算中");
        txtDistance.setText("累計距離：取得中");

        new Thread(() -> {
            try {
                GetLocationModel getLocationModel =
                        new GetLocationModel();

                ScoreProcessor scoreProcessor =
                        new ScoreProcessor();

                ScoreApiModel scoreApiModel =
                        new ScoreApiModel();

                List<RoutePoint> path =
                        getLocationModel.fetchRouteData(
                                currentWalkerId
                        );

                if (path == null) {
                    showError(
                            "移動経路を取得できませんでした"
                    );
                    return;
                }

                double totalDistance =
                        scoreProcessor.calculateTotalDistance(
                                path
                        );

                int calculatedScore =
                        scoreProcessor
                                .calculateScoreFromTotalDistance(
                                        totalDistance
                                );

                ScoreInfo scoreInfo =
                        new ScoreInfo(
                                currentWalkerId,
                                calculatedScore
                        );

                boolean saveResult =
                        scoreApiModel.saveScore(scoreInfo);

                Log.d(
                        "SCORE_PROCESS",
                        "walker_id="
                                + currentWalkerId
                                + ", routeCount="
                                + path.size()
                                + ", totalDistance="
                                + totalDistance
                                + ", calculatedScore="
                                + calculatedScore
                                + ", saveResult="
                                + saveResult
                );

                runOnUiThread(() -> {
                    txtScore.setText(
                            "スコア："
                                    + calculatedScore
                                    + "点"
                    );

                    txtDistance.setText(
                            "累計距離："
                                    + String.format(
                                    Locale.JAPAN,
                                    "%.1f",
                                    totalDistance
                            )
                                    + "m"
                    );

                    if (!saveResult) {
                        Log.e(
                                "SCORE_PROCESS",
                                "スコアの保存に失敗しました"
                        );
                    }
                });

            } catch (Exception e) {
                Log.e(
                        "SCORE_PROCESS",
                        "スコア処理に失敗しました",
                        e
                );

                showError(
                        "スコアを計算できませんでした"
                );
            }
        }).start();
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            txtScore.setText(message);
            txtDistance.setText(
                    "累計距離：取得失敗"
            );
        });
    }
}
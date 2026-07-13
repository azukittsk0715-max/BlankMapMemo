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

        btnBack.setOnClickListener(v -> finish());

        loadScore();
    }

    private void loadScore() {
        txtScore.setText("スコア：取得中");
        txtDistance.setText("累計距離：取得中");

        new Thread(() -> {
            try {
                ScoreApiModel scoreApiModel =
                        new ScoreApiModel();

                GetLocationModel getLocationModel =
                        new GetLocationModel();

                ScoreProcessor scoreProcessor =
                        new ScoreProcessor();

                ScoreInfo currentScoreInfo =
                        scoreApiModel.fetchScore(
                                currentWalkerId
                        );

                List<RoutePoint> path =
                        getLocationModel.fetchRouteData(
                                currentWalkerId
                        );

                if (currentScoreInfo == null) {
                    showLoadError(
                            "スコア情報を取得できませんでした"
                    );
                    return;
                }

                if (path == null) {
                    showLoadError(
                            "移動経路を取得できませんでした"
                    );
                    return;
                }

                double totalDistance =
                        scoreProcessor
                                .calculateTotalDistance(path);

                int displayScore =
                        currentScoreInfo.getScore() == null
                                ? 0
                                : currentScoreInfo.getScore();

                Log.d(
                        "SCORE_DISPLAY",
                        "walker_id="
                                + currentWalkerId
                                + ", score="
                                + displayScore
                                + ", routeCount="
                                + path.size()
                                + ", totalDistance="
                                + totalDistance
                );

                runOnUiThread(() -> {
                    txtScore.setText(
                            "スコア："
                                    + displayScore
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
                });

            } catch (Exception e) {
                Log.e(
                        "SCORE_DISPLAY",
                        "スコア表示処理に失敗しました",
                        e
                );

                showLoadError(
                        "スコア情報を取得できませんでした"
                );
            }
        }).start();
    }

    private void showLoadError(String message) {
        runOnUiThread(() -> {
            txtScore.setText(message);
            txtDistance.setText(
                    "累計距離：取得失敗"
            );
        });
    }
}
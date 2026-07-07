package com.example.gsmap.Model;

import java.sql.SQLException;
import java.util.List;

public class ScoreComponent {
    private final ScoreRepository scoreRepository;
    private final ScoreProcessor scoreProcessor;

    public ScoreComponent(ScoreRepository scoreRepository, ScoreProcessor scoreProcessor) {
        this.scoreRepository = scoreRepository;
        this.scoreProcessor = scoreProcessor;
    }

    public ScoreInfo getScore(String walkerId) throws SQLException {
        return scoreRepository.fetchOrCreateScore(walkerId);
    }

    public ScoreInfo updateScore(String walkerId, List<RoutePoint> path) throws SQLException {
        ScoreInfo currentScoreInfo = scoreRepository.fetchOrCreateScore(walkerId);
        int newScore = scoreProcessor.calcScore(currentScoreInfo, path);

        if (newScore < 0) {
            return currentScoreInfo;
        }

        ScoreInfo newScoreInfo = new ScoreInfo(walkerId, newScore);
        scoreRepository.saveScore(newScoreInfo);
        return newScoreInfo;
    }
}

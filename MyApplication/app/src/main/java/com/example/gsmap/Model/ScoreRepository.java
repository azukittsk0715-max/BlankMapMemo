package com.example.gsmap.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ScoreRepository {
    private final String url;
    private final String user;
    private final String password;

    public ScoreRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS score_info ("
                + "walker_id varchar(63) PRIMARY KEY, "
                + "score integer NOT NULL DEFAULT 0, "
                + "CONSTRAINT chk_score_info_walker_id_length CHECK (octet_length(walker_id) > 4 AND octet_length(walker_id) < 64), "
                + "CONSTRAINT chk_score_info_score_non_negative CHECK (score >= 0)"
                + ")";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public ScoreInfo fetchScore(String walkerId) throws SQLException {
        if (walkerId == null || walkerId.isEmpty()) {
            return null;
        }

        String sql = "SELECT walker_id, score FROM score_info WHERE walker_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, walkerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ScoreInfo(resultSet.getString("walker_id"), resultSet.getInt("score"));
                }
            }
        }

        return null;
    }

    public ScoreInfo fetchOrCreateScore(String walkerId) throws SQLException {
        ScoreInfo scoreInfo = fetchScore(walkerId);

        if (scoreInfo != null) {
            return scoreInfo;
        }

        ScoreInfo newScoreInfo = new ScoreInfo(walkerId, 0);
        saveScore(newScoreInfo);
        return newScoreInfo;
    }

    public boolean saveScore(ScoreInfo scoreInfo) throws SQLException {
        if (scoreInfo == null || scoreInfo.getWalkerId() == null || scoreInfo.getWalkerId().isEmpty() || scoreInfo.getScore() == null) {
            return false;
        }

        String sql = "INSERT INTO score_info (walker_id, score) VALUES (?, ?) "
                + "ON CONFLICT (walker_id) DO UPDATE SET score = EXCLUDED.score";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, scoreInfo.getWalkerId());
            statement.setInt(2, scoreInfo.getScore());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteScore(String walkerId) throws SQLException {
        if (walkerId == null || walkerId.isEmpty()) {
            return false;
        }

        String sql = "DELETE FROM score_info WHERE walker_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, walkerId);
            return statement.executeUpdate() > 0;
        }
    }
}

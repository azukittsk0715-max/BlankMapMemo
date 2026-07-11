package com.example.gsmap.Model;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

public class ScoreApiModel {

    private static final String BASE_URL = "http://172.21.33.121:7070";

    public ScoreInfo fetchScore(String walkerId) {
        HttpURLConnection conn = null;

        try {
            String encodedWalkerId = URLEncoder.encode(walkerId, "UTF-8");
            URL url = new URL(BASE_URL + "/score/get?walker_id=" + encodedWalkerId);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int statusCode = conn.getResponseCode();

            if (statusCode < 200 || statusCode >= 300) {
                return new ScoreInfo(walkerId, 0);
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8")
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());

            if (!json.optBoolean("success", false)) {
                return new ScoreInfo(walkerId, 0);
            }

            int score = json.optInt("score", 0);

            return new ScoreInfo(walkerId, score);

        } catch (Exception e) {
            e.printStackTrace();
            return new ScoreInfo(walkerId, 0);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public boolean saveScore(ScoreInfo scoreInfo) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(BASE_URL + "/score/save");

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            JSONObject body = new JSONObject();
            body.put("walker_id", scoreInfo.getWalkerId());
            body.put("score", scoreInfo.getScore());

            OutputStream os = conn.getOutputStream();
            os.write(body.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            int statusCode = conn.getResponseCode();

            if (statusCode < 200 || statusCode >= 300) {
                return false;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8")
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());

            return json.optBoolean("success", false);

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
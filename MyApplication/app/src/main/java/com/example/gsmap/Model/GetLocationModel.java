package com.example.gsmap.Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetLocationModel {

    private static final String BASE_URL = "http://172.21.33.121:7070";

    public List<RoutePoint> fetchRouteData(String walkerId) {
        List<RoutePoint> path = new ArrayList<>();
        HttpURLConnection conn = null;

        try {
            String encodedWalkerId = URLEncoder.encode(walkerId, "UTF-8");
            URL url = new URL(BASE_URL + "/route/get?walker_id=" + encodedWalkerId);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int statusCode = conn.getResponseCode();

            if (statusCode < 200 || statusCode >= 300) {
                return path;
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
                return path;
            }

            JSONArray routes = json.getJSONArray("routes");

            for (int i = 0; i < routes.length(); i++) {
                JSONObject route = routes.getJSONObject(i);

                double latitude = route.getDouble("latitude");
                double longitude = route.getDouble("longitude");
                String cTime = route.getString("c_time");

                path.add(new RoutePoint(latitude, longitude, cTime));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return path;
    }
}
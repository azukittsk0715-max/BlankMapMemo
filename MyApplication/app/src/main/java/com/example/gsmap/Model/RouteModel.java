package com.example.gsmap.Model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RouteModel {

    // サーバーのアドレス（認証と同じサーバー）
    private static final String BASE_URL = "http://172.21.33.121:7070";

    // 1地点を表す入れ物
    public static class RoutePoint {
        public double latitude;
        public double longitude;
        public RoutePoint(double lat, double lon) {
            this.latitude = lat;
            this.longitude = lon;
        }
    }

    // 移動経路を1件保存する（/route/save を呼ぶ）
    public boolean saveRoute(String walkerId, double lat, double lon) {
        try {
            URL url = new URL(BASE_URL + "/route/save");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 現在時刻を "yyyy-MM-dd HH:mm:ss" 形式で作る
            String cTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN)
                    .format(new Date());

            JSONObject json = new JSONObject();
            json.put("walker_id", walkerId);
            json.put("latitude", lat);
            json.put("longitude", lon);
            json.put("c_time", cTime);

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 保存済みの移動経路を全部取得する（/route/get を呼ぶ）
    public List<RoutePoint> getRoutes(String walkerId) {
        List<RoutePoint> result = new ArrayList<>();
        try {
            String query = "walker_id=" + URLEncoder.encode(walkerId, "UTF-8");
            URL url = new URL(BASE_URL + "/route/get?" + query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            conn.disconnect();

            JSONObject json = new JSONObject(sb.toString());
            JSONArray routes = json.getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {
                JSONObject p = routes.getJSONObject(i);
                result.add(new RoutePoint(
                        p.getDouble("latitude"),
                        p.getDouble("longitude")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
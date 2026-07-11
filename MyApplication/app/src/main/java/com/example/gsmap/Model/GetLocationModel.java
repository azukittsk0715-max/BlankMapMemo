// 6/24榎本
// 【PostgreSQL版】ウォーカー情報管理部（C6）
// サーバAPI（http://172.21.33.121:7070）経由でPostgreSQLにアクセスする。

package com.example.gsmap.Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GetLocationModel {

        // 認証APIのベースURL（サーバのIPアドレス）
        private static final String BASE_URL = "http://172.21.33.121:7070";


        public static class RouteData {

            private final double latitude;
        private final double longitude;
        private final String cTime;

        public RouteData(double latitude,
                         double longitude,
                         String cTime) {

            this.latitude = latitude;
            this.longitude = longitude;
            this.cTime = cTime;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getCTime() {
            return cTime;
        }
    }


    // --- ここから下は共通の通信処理 ---

    // 指定したエンドポイントにIDとパスワードをPOSTし、結果のJSONを返す

    /**
     * 指定ユーザーの移動経路を取得する
     */
    public List<RouteData> fetchRouteData(String walkerId) {

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        Future<List<RouteData>> future =
                executor.submit(() -> {

                    List<RouteData> routeList =
                            new ArrayList<>();

                    try {

                        URL url = new URL(
                                BASE_URL +
                                        "/route/get?walker_id=" +
                                        walkerId
                        );

                        HttpURLConnection conn =
                                (HttpURLConnection)
                                        url.openConnection();

                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(5000);

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                conn.getInputStream(),
                                                "UTF-8"
                                        )
                                );

                        StringBuilder response =
                                new StringBuilder();

                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        reader.close();

                        JSONObject json =
                                new JSONObject(response.toString());

                        if (json.optBoolean("success")) {

                            JSONArray routes =
                                    json.getJSONArray("routes");

                            for (int i = 0;
                                 i < routes.length();
                                 i++) {

                                JSONObject route =
                                        routes.getJSONObject(i);

                                double latitude =
                                        route.getDouble("latitude");

                                double longitude =
                                        route.getDouble("longitude");

                                String cTime =
                                        route.getString("c_time");

                                routeList.add(
                                        new RouteData(
                                                latitude,
                                                longitude,
                                                cTime
                                        )
                                );
                            }
                        }

                        conn.disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return routeList;
                });

        try {
            return future.get();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();

        } finally {
            executor.shutdown();
        }
    }

}
package com.example.gsmap.Model;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SaveLocationModel {

    // APIサーバ
    private static final String BASE_URL =
            "http://172.21.33.121:7070";

    /**
     * 移動経路情報を保存する
     */
    public boolean saveRoutePoint(
            String walkerId,
            double latitude,
            double longitude,
            String cTime) {

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        Future<Boolean> future =
                executor.submit(() -> {

                    try {

                        URL url =
                                new URL(
                                        BASE_URL +
                                                "/route/save"
                                );

                        HttpURLConnection conn =
                                (HttpURLConnection)
                                        url.openConnection();

                        conn.setRequestMethod("POST");
                        conn.setRequestProperty(
                                "Content-Type",
                                "application/json"
                        );

                        conn.setDoOutput(true);
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(5000);

                        // 送信JSON作成
                        JSONObject body =
                                new JSONObject();

                        body.put(
                                "walker_id",
                                walkerId
                        );

                        body.put(
                                "latitude",
                                latitude
                        );

                        body.put(
                                "longitude",
                                longitude
                        );

                        body.put(
                                "c_time",
                                cTime
                        );

                        // JSON送信
                        OutputStream os =
                                conn.getOutputStream();

                        os.write(
                                body.toString()
                                        .getBytes("UTF-8")
                        );

                        os.flush();
                        os.close();

                        // レスポンス取得
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
                        conn.disconnect();

                        JSONObject result =
                                new JSONObject(
                                        response.toString()
                                );

                        return result.optBoolean(
                                "success",
                                false
                        );

                    } catch (Exception e) {

                        e.printStackTrace();

                        return false;
                    }
                });

        try {

            return future.get();

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        } finally {

            executor.shutdown();
        }
    }
}
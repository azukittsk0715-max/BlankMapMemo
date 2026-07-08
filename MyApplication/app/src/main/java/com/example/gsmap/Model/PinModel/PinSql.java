package com.example.gsmap.Model.PinModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PinSql {

    // サーバURL
    private static final String BASE_URL =
            "http://172.21.33.121:7070";

    // ------------------------------
    // ピン保存
    // ------------------------------
    public boolean savePin(String walkerId,
                           double latitude,
                           double longitude,
                           String memo) {

        JSONObject result = postToApi(
                "/pin/save",
                walkerId,
                latitude,
                longitude,
                memo
        );

        return result != null &&
                result.optBoolean("success", false);
    }

    // ------------------------------
    // ピン取得
    // ------------------------------
    public JSONArray getPins(String walkerId) {

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        Future<JSONArray> future =
                executor.submit(new Callable<JSONArray>() {

                    @Override
                    public JSONArray call() {

                        try {

                            String urlString =
                                    BASE_URL
                                            + "/pin/get?walker_id="
                                            + URLEncoder.encode(
                                            walkerId,
                                            "UTF-8");

                            URL url = new URL(urlString);

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
                            conn.disconnect();

                            JSONObject json =
                                    new JSONObject(
                                            response.toString());

                            if (json.optBoolean("success")) {

                                return json.getJSONArray("pins");

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                        return new JSONArray();

                    }

                });

        try {

            return future.get();

        } catch (Exception e) {

            e.printStackTrace();

            return new JSONArray();

        } finally {

            executor.shutdown();

        }

    }

    // ------------------------------
    // 共通POST通信
    // ------------------------------
    private JSONObject postToApi(String endpoint,
                                 String walkerId,
                                 double latitude,
                                 double longitude,
                                 String memo) {

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        Future<JSONObject> future =
                executor.submit(new Callable<JSONObject>() {

                    @Override
                    public JSONObject call() {

                        try {

                            URL url =
                                    new URL(BASE_URL + endpoint);

                            HttpURLConnection conn =
                                    (HttpURLConnection)
                                            url.openConnection();

                            conn.setRequestMethod("POST");
                            conn.setRequestProperty(
                                    "Content-Type",
                                    "application/json");

                            conn.setDoOutput(true);
                            conn.setConnectTimeout(5000);
                            conn.setReadTimeout(5000);

                            JSONObject body =
                                    new JSONObject();

                            body.put("walker_id", walkerId);
                            body.put("latitude", latitude);
                            body.put("longitude", longitude);
                            body.put("memo", memo);

                            OutputStream os =
                                    conn.getOutputStream();

                            os.write(body.toString().getBytes("UTF-8"));

                            os.flush();
                            os.close();

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

                            return new JSONObject(
                                    response.toString());

                        } catch (Exception e) {

                            e.printStackTrace();

                            return null;

                        }

                    }

                });

        try {

            return future.get();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            executor.shutdown();

        }

    }

}
package com.example.gsmap.Model;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GetScoreModel {

    private static final String BASE_URL =
            "http://172.21.33.121:7070";

    public int getScore(String walkerId) {

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        Future<Integer> future =
                executor.submit(() -> {

                    try {

                        URL url =
                                new URL(
                                        BASE_URL +
                                                "/score/get?walker_id=" +
                                                walkerId
                                );

                        HttpURLConnection conn =
                                (HttpURLConnection)
                                        url.openConnection();

                        conn.setRequestMethod("GET");

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

                        if (result.optBoolean("success")) {

                            return result.optInt(
                                    "score",
                                    0
                            );
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    return 0;
                });

        try {

            return future.get();

        } catch (Exception e) {

            e.printStackTrace();
            return 0;

        } finally {

            executor.shutdown();
        }
    }
}
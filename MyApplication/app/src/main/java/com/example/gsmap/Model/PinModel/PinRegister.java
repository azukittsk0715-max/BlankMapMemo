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

public class PinRegister {

    private static final String BASE_URL =
            "http://172.21.33.121:7070";

    public boolean savePin(
            String walkerId,
            double latitude,
            double longitude,
            String memo) {

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        Future<Boolean> future =
                executor.submit(() -> {

                    try {

                        URL url =
                                new URL(
                                        BASE_URL +
                                                "/pin/save"
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
                                "memo",
                                memo
                        );

                        OutputStream os =
                                conn.getOutputStream();

                        os.write(
                                body.toString()
                                        .getBytes("UTF-8")
                        );

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

//package com.example.gsmap.Model.PinModel;
//
//import android.util.Log;
//
//public class PinRegister {
//
//    public boolean savePin(String walkerId,
//                           double latitude,
//                           double longitude,
//                           String memo) {
//
//        Log.d("PinTest", "walkerId=" + walkerId);
//        Log.d("PinTest", "latitude=" + latitude);
//        Log.d("PinTest", "longitude=" + longitude);
//        Log.d("PinTest", "memo=" + memo);
//
//        return true;
//    }
//}

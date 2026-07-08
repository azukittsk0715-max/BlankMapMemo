// 6/24榎本
// 【PostgreSQL版】ウォーカー情報管理部（C6）
// サーバAPI（http://172.21.33.121:7070）経由でPostgreSQLにアクセスする。

package com.example.gsmap.Model;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GetLocationModel {

    // 認証APIのベースURL（サーバのIPアドレス）
    private static final String BASE_URL = "http://172.21.33.121:7070";

    // ① 新規登録：APIの /register にIDとパスワードを送る
    public boolean fetchGetLocation(String walkerId, String password) {
        JSONObject result = postToApi("/route/get", walkerId, password);
        return result != null && result.optBoolean("success", false);
    }

    // ② 情報取得：このAPI構成では単独取得は使わないが、設計の形を保つため残す
    public String getWalkerPassword(String walkerId) {
        // このAPI設計では照合はサーバ側で行うため、ここでは使用しない
        return null;
    }

    // ③ ログイン照合：APIの /login にIDとパスワードを送る
    public boolean verifyPassword(String walkerId, String inputPassword) {
        JSONObject result = postToApi("/login", walkerId, inputPassword);
        return result != null && result.optBoolean("success", false);
    }

    // --- ここから下は共通の通信処理 ---

    // 指定したエンドポイントにIDとパスワードをPOSTし、結果のJSONを返す
    private JSONObject postToApi(String endpoint, String walkerId, String password) {
        // 別スレッドで通信を実行する（メインスレッドで通信するとアプリが落ちるため）
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JSONObject> future = executor.submit(new Callable<JSONObject>() {
            @Override
            public JSONObject call() {
                try {
                    URL url = new URL(BASE_URL + endpoint);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    // 送信するJSONを作る
                    JSONObject body = new JSONObject();
                    body.put("walker_id", walkerId);
                    body.put("password", password);

                    // JSONを送信
                    OutputStream os = conn.getOutputStream();
                    os.write(body.toString().getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // 応答を読み取る
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    conn.disconnect();

                    // 受け取った文字列をJSONに変換して返す
                    return new JSONObject(response.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        try {
            return future.get(); // 通信が終わるまで待って、結果を受け取る
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            executor.shutdown();
        }
    }
}
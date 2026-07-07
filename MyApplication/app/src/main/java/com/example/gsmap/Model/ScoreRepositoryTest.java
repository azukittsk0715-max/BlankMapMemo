package com.example.gsmap.Model;

public class ScoreRepositoryTest {
    public static void main(String[] args) throws Exception {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            System.out.println("DB_URL, DB_USER, DB_PASSWORD を設定してください");
            return;
        }

        ScoreRepository repository = new ScoreRepository(url, user, password);

        String walkerId = "user001";

        ScoreInfo before = repository.fetchScore(walkerId);

        if (before == null) {
            System.out.println("取得結果: null");
        } else {
            System.out.println("取得結果: " + before.getWalkerId() + " " + before.getScore());
        }

        ScoreInfo saveData = new ScoreInfo(walkerId, 250);
        boolean saveResult = repository.saveScore(saveData);

        System.out.println("保存結果: " + saveResult);

        ScoreInfo after = repository.fetchScore(walkerId);

        if (after == null) {
            System.out.println("更新後取得結果: null");
        } else {
            System.out.println("更新後取得結果: " + after.getWalkerId() + " " + after.getScore());
        }
    }
}
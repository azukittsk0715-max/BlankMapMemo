//package com.example.gsmap.Model;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ScoreComponentTest {
//    public static void main(String[] args) throws Exception {
//        String url = System.getenv("DB_URL");
//        String user = System.getenv("DB_USER");
//        String password = System.getenv("DB_PASSWORD");
//
//        if (url == null || user == null || password == null) {
//            System.out.println("DB_URL, DB_USER, DB_PASSWORD を設定してください");
//            return;
//        }
//
//        ScoreRepository repository = new ScoreRepository(url, user, password);
//        ScoreProcessor processor = new ScoreProcessor();
//
//        String walkerId = "user001";
//
//        ScoreInfo currentScoreInfo = repository.fetchScore(walkerId);
//
//        if (currentScoreInfo == null) {
//            currentScoreInfo = new ScoreInfo(walkerId, 0);
//            repository.saveScore(currentScoreInfo);
//        }
//
//        System.out.println("計算前スコア: " + currentScoreInfo.getScore());
//
//        List<RoutePoint> path = new ArrayList<>();
//        path.add(new RoutePoint(35.681236, 139.767125, "2026-06-16T10:00:00"));
//        path.add(new RoutePoint(35.681500, 139.768000, "2026-06-16T10:00:05"));
//        path.add(new RoutePoint(35.682000, 139.769000, "2026-06-16T10:00:10"));
//
//        double totalDistance = processor.calculateTotalDistance(path);
//        int newScore = processor.calcScore(currentScoreInfo, path);
//
//        System.out.println("累計距離: " + totalDistance);
//        System.out.println("計算後スコア: " + newScore);
//
//        ScoreInfo newScoreInfo = new ScoreInfo(walkerId, newScore);
//        boolean saveResult = repository.saveScore(newScoreInfo);
//
//        System.out.println("保存結果: " + saveResult);
//
//        ScoreInfo afterScoreInfo = repository.fetchScore(walkerId);
//        System.out.println("DB保存後スコア: " + afterScoreInfo.getScore());
//    }
//}
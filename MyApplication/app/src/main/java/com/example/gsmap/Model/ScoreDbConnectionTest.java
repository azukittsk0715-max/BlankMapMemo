//package com.example.gsmap.Model;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ScoreDbConnectionTest {
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
//        ScoreComponent component = new ScoreComponent(repository, processor);
//
//        repository.createTableIfNotExists();
//
//        String walkerId = "user001";
//        ScoreInfo before = component.getScore(walkerId);
//        System.out.println("更新前: " + before.getWalkerId() + " " + before.getScore());
//
//        List<RoutePoint> path = new ArrayList<>();
//        path.add(new RoutePoint(35.681236, 139.767125, "2026-06-16T10:00:00"));
//        path.add(new RoutePoint(35.681500, 139.768000, "2026-06-16T10:00:05"));
//        path.add(new RoutePoint(35.682000, 139.769000, "2026-06-16T10:00:10"));
//
//        ScoreInfo after = component.updateScore(walkerId, path);
//        System.out.println("更新後: " + after.getWalkerId() + " " + after.getScore());
//    }
//}

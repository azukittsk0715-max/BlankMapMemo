package com.example.gsmap.Model;

import java.util.List;

public class ScoreProcessor {

    private static final double EARTH_RADIUS_METERS = 6371000.0;
    private static final double METERS_PER_POINT = 10.0;

    public double calculateDistance(
            RoutePoint previous,
            RoutePoint current) {

        if (previous == null || current == null) {
            return 0.0;
        }

        double previousLatitude =
                Math.toRadians(previous.getLatitude());

        double currentLatitude =
                Math.toRadians(current.getLatitude());

        double latitudeDifference =
                Math.toRadians(
                        current.getLatitude()
                                - previous.getLatitude()
                );

        double longitudeDifference =
                Math.toRadians(
                        current.getLongitude()
                                - previous.getLongitude()
                );

        double a =
                Math.sin(latitudeDifference / 2.0)
                        * Math.sin(latitudeDifference / 2.0)
                        + Math.cos(previousLatitude)
                        * Math.cos(currentLatitude)
                        * Math.sin(longitudeDifference / 2.0)
                        * Math.sin(longitudeDifference / 2.0);

        double c =
                2.0 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1.0 - a)
                );

        return EARTH_RADIUS_METERS * c;
    }

    public double calculateTotalDistance(
            List<RoutePoint> path) {

        if (path == null || path.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;

        for (int i = 1; i < path.size(); i++) {
            RoutePoint previous = path.get(i - 1);
            RoutePoint current = path.get(i);

            totalDistance +=
                    calculateDistance(
                            previous,
                            current
                    );
        }

        return totalDistance;
    }

    public int calculateScoreFromTotalDistance(
            double totalDistance) {

        if (totalDistance <= 0.0) {
            return 0;
        }

        return (int) Math.floor(
                totalDistance / METERS_PER_POINT
        );
    }

    public int calculateScoreFromPath(
            List<RoutePoint> path) {

        double totalDistance =
                calculateTotalDistance(path);

        return calculateScoreFromTotalDistance(
                totalDistance
        );
    }

    public int calcScore(
            ScoreInfo scoreInfo,
            List<RoutePoint> path) {

        return calculateScoreFromPath(path);
    }
}
import java.util.ArrayList;
import java.util.List;

public class ScoreProcessor {

    // TODO: スコア計算式は仕様確定後に変更する。
    // 現在は10mにつき1点として計算する。
    private static final double METERS_PER_POINT = 10.0;

    // 地球の半径(m)
    private static final double EARTH_RADIUS = 6378137.0;

    /**
     * C5-M1 スコア取得
     * C8 スコア情報管理部からスコアを受け取る想定。
     * 現段階ではC8未実装のため、仮で0点のScoreInfoを返す。
     */
    public ScoreInfo getScore(String walker_id) {
        if (walker_id == null || walker_id.isEmpty()) {
            return null;
        }

        // TODO: C8 スコア情報管理部と接続後、DB/APIから取得した値に置き換える。
        return new ScoreInfo(walker_id, 0);
    }

    /**
     * C5-M2 移動経路取得
     * C7 移動経路情報管理部から移動経路情報を取得する想定。
     * 現段階ではC7未実装のため、空のリストを返す。
     */
    public List<RoutePoint> getPath(String walker_id) {
        if (walker_id == null || walker_id.isEmpty()) {
            return new ArrayList<>();
        }

        // TODO: C7 移動経路情報管理部と接続後、DB/APIから取得した経路に置き換える。
        return new ArrayList<>();
    }

    /**
     * C5-M3 スコア計算の補助処理。
     * 2地点間の距離をHaversine式で計算する。
     *
     * @param previous 前の地点
     * @param current  現在の地点
     * @return 2地点間の距離(m)
     */
    public double calculateDistance(RoutePoint previous, RoutePoint current) {
        if (previous == null || current == null) {
            return 0.0;
        }

        double lat1 = Math.toRadians(previous.getLatitude());
        double lon1 = Math.toRadians(previous.getLongitude());
        double lat2 = Math.toRadians(current.getLatitude());
        double lon2 = Math.toRadians(current.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double sinLat = Math.sin(dLat / 2.0);
        double sinLon = Math.sin(dLon / 2.0);

        double a =
                sinLat * sinLat
                        + Math.cos(lat1) * Math.cos(lat2)
                        * sinLon * sinLon;

        // 浮動小数点誤差対策
        a = Math.max(0.0, Math.min(1.0, a));

        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * C5-M3 スコア計算の補助処理。
     * 移動経路リスト内の隣り合う地点間距離を合計する。
     *
     * @param path 移動経路
     * @return 累計移動距離(m)
     */
    public double calculateTotalDistance(List<RoutePoint> path) {
        if (path == null || path.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;

        for (int i = 1; i < path.size(); i++) {
            RoutePoint previous = path.get(i - 1);
            RoutePoint current = path.get(i);

            double distance = calculateDistance(previous, current);
            totalDistance += distance;
        }

        return totalDistance;
    }

    /**
     * C5-M3 スコア計算。
     * 既存スコアと移動経路情報から新しいスコアを計算する。
     *
     * @param scoreInfo 現在のスコア情報
     * @param path      移動経路情報
     * @return 新しいスコア。エラー時は-1。
     */
    public int calcScore(ScoreInfo scoreInfo, List<RoutePoint> path) {
        if (scoreInfo == null || scoreInfo.getScore() == null) {
            return -1;
        }

        int currentScore = scoreInfo.getScore();

        if (path == null || path.size() < 2) {
            return currentScore;
        }

        double totalDistance = calculateTotalDistance(path);

        int addScore = (int) (totalDistance / METERS_PER_POINT);

        return currentScore + addScore;
    }
}
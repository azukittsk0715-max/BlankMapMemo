import java.util.*;

public class ScoreProcessor{
    public ScoreInfo getScore(String walker_id){
        /* C8スコア情報管理部からスコアを受け取る */
        ScoreInfo scoreinfo = null;
        if(scoreinfo != null){
            /* M3スコア計算へスコアを渡す */
            return scoreinfo;//後で
        }else{
            return null; /* error */
        }
    }

    public List<RoutePoint> getPath(String walker_id){
        /* C7 移動経路情報管理部から移動経路情報を取得してM3 スコア計算へ渡す */
        List<RoutePoint> list = new ArrayList<>();
        return list;//後で
    }

//    public double calculateDistance(RoutePoint previous, RoutePoint current){
//        /*地球の半径(m)*/
//        double EARTH_RAD = 6378137.0;
//
//        double x1 = previous.longitude * Math.PI / 180;
//        double y1 = previous.latitude * Math.PI / 180;
//        double x2 = current.longitude * Math.PI / 180;
//        double y2 = current.latitude * Math.PI / 180;
//
//        return EARTH_RAD * Math.acos(Math.sin(y1) * Math.sin(y2) + Math.cos(y1) * Math.cos(y2) * Math.cos(x2 - x1));
//    }

    public double calculateDistance(RoutePoint previous, RoutePoint current) {
        if (previous == null || current == null) {
            return 0.0;
        }

        // 地球の半径(m)
        final double EARTH_RADIUS = 6378137.0;

        // 緯度・経度をラジアンに変換
        double lat1 = Math.toRadians(previous.latitude);
        double lon1 = Math.toRadians(previous.longitude);
        double lat2 = Math.toRadians(current.latitude);
        double lon2 = Math.toRadians(current.longitude);

        // 緯度差・経度差
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Haversine式
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(lat1) * Math.cos(lat2)
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 2点間距離(m)
        return EARTH_RADIUS * c;
    }

    public double calculateTotalDistance(List<RoutePoint> path){
        double distance = 0, totaldistance = 0;
        RoutePoint previous, current;

        for(int i=1;i<path.size();i++){
            previous = path.get(i-1);
            current = path.get(i);
            distance = calculateDistance(previous, current);
            totaldistance = totaldistance + distance;
        }
        return totaldistance;
    }

    public int calcScore(ScoreInfo scoreinfo, List<RoutePoint> path){
        if(scoreinfo == null){
            return -1;
        }

        int currentscore = scoreinfo.score;
        if(path == null){
            return currentscore;
        }

        double totaldistance = calculateTotalDistance(path);

        // TODO: スコア計算式は仕様確定後に変更する。現在は10mにつき1点
        int addscore = (int)(totaldistance / 10);
        int newscore = currentscore + addscore;
        return newscore;
    }
}
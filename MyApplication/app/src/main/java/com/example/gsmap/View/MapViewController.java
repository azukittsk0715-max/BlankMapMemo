package com.example.gsmap;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

public class MapViewController {

    private MapView mapView;
    boolean isFirstUpdate = true;

    // 直前の現在地マーカー（更新のたびにこれだけ消して置き直す）
    private Marker currentMarker;

    public MapViewController(MapView mapView) {
        this.mapView = mapView;
    }

    public void initMap() {

        XYTileSource tileSource = new XYTileSource(
                "GSI",
                5,
                18,
                256,
                ".png",
                new String[]{
                        "https://cyberjapandata.gsi.go.jp/xyz/std/"
                }
        );

        mapView.setTileSource(tileSource);
        mapView.setMultiTouchControls(true);
    }

    public void updateLocation(double lat, double lon) {

        GeoPoint point = new GeoPoint(lat, lon);

        if (isFirstUpdate) {
            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(point);
            isFirstUpdate = false;
        }

        // 訪問済みエリアとして円を塗る（マーカーより先に描画）
        addVisitedArea(lat, lon);

        // 現在地マーカーの更新（前回のマーカーだけ削除して置き直す）
        if (currentMarker != null) {
            mapView.getOverlays().remove(currentMarker);
        }

        currentMarker = new Marker(mapView);
        currentMarker.setPosition(point);
        currentMarker.setTitle("現在地");

        mapView.getOverlays().add(currentMarker);

        mapView.invalidate();
    }

    // 歩いた場所を半透明の円で塗りつぶす（訪問済みエリアの描画）
    public void addVisitedArea(double lat, double lon) {
        Polygon circle = new Polygon();
        circle.setPoints(Polygon.pointsAsCircle(
                new GeoPoint(lat, lon),
                30 // 半径30メートル
        ));
        circle.getFillPaint().setColor(0x552196F3); // 半透明の青
        circle.getOutlinePaint().setStrokeWidth(0f); // 枠線なし

        mapView.getOverlayManager().add(circle);
    }
}
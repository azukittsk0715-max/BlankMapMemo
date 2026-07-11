package com.example.gsmap;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapViewController {

    private MapView mapView;
    boolean isFirstUpdate = true;

    private Marker currentMarker;
    private FogOverlay fogOverlay; // 霧レイヤー

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

        // 霧レイヤーを地図に追加
        fogOverlay = new FogOverlay();
        mapView.getOverlays().add(fogOverlay);
    }

    public void updateLocation(double lat, double lon) {

        GeoPoint point = new GeoPoint(lat, lon);

        if (isFirstUpdate) {
            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(point);
            isFirstUpdate = false;
        }

        // 訪問済みエリア（霧に穴を開ける）
        addVisitedArea(lat, lon);

        // 現在地マーカーの更新
        if (currentMarker != null) {
            mapView.getOverlays().remove(currentMarker);
        }

        currentMarker = new Marker(mapView);
        currentMarker.setPosition(point);
        currentMarker.setTitle("現在地");

        mapView.getOverlays().add(currentMarker);

        mapView.invalidate();
    }

    // 歩いた場所の霧を晴らす（訪問済みエリアの登録）
    public void addVisitedArea(double lat, double lon) {
        fogOverlay.addVisitedPoint(lat, lon);
        mapView.invalidate();
    }
}
package com.example.gsmap;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapViewController {

    private MapView mapView;

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

        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(point);

        // マーカー更新
        mapView.getOverlays().clear();

        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle("現在地");

        mapView.getOverlays().add(marker);
    }
}

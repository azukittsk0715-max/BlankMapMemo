package com.example.gsmap.View;

import android.content.Context;
import android.content.Intent;

import com.example.gsmap.Model.PinModel.PinAddActivity;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

public class MapViewController {

    private final Context context;
    private final MapView mapView;
    private final String walkerId;
    boolean isFirstUpdate = true;

    // 直前の現在地マーカー（更新のたびにこれだけ消して置き直す）
    private Marker currentMarker;
    private FogOverlay fogOverlay; // 霧レイヤー
    public MapViewController(
            Context context,
            MapView mapView,
            String walkerId) {

        this.context = context;
        this.mapView = mapView;
        this.walkerId = walkerId;

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

        MapEventsReceiver receiver = new MapEventsReceiver() {

        @Override
        public boolean singleTapConfirmedHelper(GeoPoint p) {

            Intent intent =
                    new Intent(context, PinAddActivity.class);

            intent.putExtra(
                    "LATITUDE",
                    p.getLatitude());

            intent.putExtra(
                    "LONGITUDE",
                    p.getLongitude());

            intent.putExtra(
                    "WALKER_ID",
                    walkerId);

            context.startActivity(intent);

            return true;
        }

        @Override
        public boolean longPressHelper(GeoPoint p) {

            return false;

        }
    };

    MapEventsOverlay overlay =
            new MapEventsOverlay(receiver);

        mapView.getOverlays().add(overlay);

        mapView.invalidate();

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
        fogOverlay.addVisitedPoint(lat, lon);
        mapView.invalidate();
    }
}
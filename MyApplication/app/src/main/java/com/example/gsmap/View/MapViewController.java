package com.example.gsmap.View;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.gsmap.R;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class MapViewController {

    public interface OnMapTapListener {

        void onMapTap(
                double lat,
                double lon
        );
    }

    private final Context context;
    private final MapView mapView;
    private final String walkerId;

    private OnMapTapListener onMapTapListener;

    boolean isFirstUpdate = true;

    // 現在地マーカー
    private Marker currentMarker;

    // 霧レイヤー
    private FogOverlay fogOverlay;

    public MapViewController(
            Context context,
            MapView mapView,
            String walkerId) {

        this.context = context;
        this.mapView = mapView;
        this.walkerId = walkerId;
    }

    public void setOnMapTapListener(
            OnMapTapListener listener) {

        this.onMapTapListener = listener;
    }

    public void initMap() {

        XYTileSource tileSource =
                new XYTileSource(
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

        // 霧レイヤー追加
        fogOverlay = new FogOverlay();
        mapView.getOverlays().add(fogOverlay);

        MapEventsReceiver receiver =
                new MapEventsReceiver() {

                    @Override
                    public boolean singleTapConfirmedHelper(
                            GeoPoint p) {

                        if (onMapTapListener != null) {

                            onMapTapListener.onMapTap(
                                    p.getLatitude(),
                                    p.getLongitude()
                            );
                        }

                        return true;
                    }

                    @Override
                    public boolean longPressHelper(
                            GeoPoint p) {

                        return false;
                    }
                };

        MapEventsOverlay overlay =
                new MapEventsOverlay(receiver);

        mapView.getOverlays().add(overlay);

        mapView.invalidate();
    }

    public void updateLocation(
            double lat,
            double lon) {

        GeoPoint point =
                new GeoPoint(lat, lon);

        if (isFirstUpdate) {

            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(point);

            isFirstUpdate = false;
        }

        // 訪問済みエリア更新
        addVisitedArea(lat, lon);

        // 現在地マーカー更新
        if (currentMarker != null) {

            mapView.getOverlays()
                    .remove(currentMarker);
        }

        currentMarker =
                new Marker(mapView);

        currentMarker.setPosition(point);
        currentMarker.setTitle("現在地");

        Drawable currentIcon =
                ContextCompat.getDrawable(
                        context,
                        R.drawable.pin_red
                );

        currentMarker.setIcon(currentIcon);

        mapView.getOverlays()
                .add(currentMarker);

        mapView.invalidate();
    }

    /**
     * ピン表示
     */
    public void addPin(
            double lat,
            double lon,
            String memo) {

        GeoPoint point =
                new GeoPoint(lat, lon);

        Marker marker =
                new Marker(mapView);

        marker.setPosition(point);

        marker.setTitle(memo);

        mapView.getOverlays().add(marker);

        mapView.invalidate();
    }

    /**
     * 霧晴らし
     */
    public void addVisitedArea(
            double lat,
            double lon) {

        fogOverlay.addVisitedPoint(
                lat,
                lon
        );

        mapView.invalidate();
    }
}
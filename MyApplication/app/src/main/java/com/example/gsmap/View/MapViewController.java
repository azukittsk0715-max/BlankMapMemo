package com.example.gsmap.View;

import android.content.Context;
import android.content.Intent;

import com.example.gsmap.Model.PinModel.GetPinInfo;
import com.example.gsmap.Model.PinModel.PinAddActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class MapViewController {

    private MapView mapView;
    private Context context;
    private String walkerId;

    private boolean isFirstUpdate = true;

    public MapViewController(Context context,
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
    }

    public void updateLocation(double lat, double lon) {

        GeoPoint point =
                new GeoPoint(lat, lon);

        if (isFirstUpdate) {

            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(point);

            isFirstUpdate = false;
        }

        // Overlayを一旦クリア
        mapView.getOverlays().clear();

        // タップイベントを再登録
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

        mapView.getOverlays().add(
                new MapEventsOverlay(receiver)
        );

        // 現在地マーカー
        Marker current =
                new Marker(mapView);

        current.setPosition(point);
        current.setTitle("現在地");

        mapView.getOverlays().add(current);

        // 保存済みピン表示
        try {

            GetPinInfo pinInfo =
                    new GetPinInfo();

            JSONArray array =
                    pinInfo.getPins(walkerId);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj =
                        array.getJSONObject(i);

                Marker marker =
                        new Marker(mapView);

                marker.setPosition(
                        new GeoPoint(
                                obj.getDouble("latitude"),
                                obj.getDouble("longitude")
                        )
                );

                marker.setTitle(
                        obj.getString("memo")
                );

                mapView.getOverlays().add(marker);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        mapView.invalidate();
    }
}
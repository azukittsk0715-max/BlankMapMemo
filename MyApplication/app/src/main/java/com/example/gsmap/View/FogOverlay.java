package com.example.gsmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import java.util.ArrayList;
import java.util.List;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

public class FogOverlay extends Overlay {

    private final List<GeoPoint> visitedPoints = new ArrayList<>();

    private static final int FOG_COLOR = 0xE0FFFFFF; // E8よりほんの少し明るめ
    private static final double RADIUS_METERS = 30;   // 解放される半径
    private final Paint clearPaint = new Paint();

    public FogOverlay() {
        clearPaint.setAntiAlias(true);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void addVisitedPoint(double lat, double lon) {
        visitedPoints.add(new GeoPoint(lat, lon));
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow) return;

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        Bitmap fogBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas fogCanvas = new Canvas(fogBitmap);

        // ① 画面全体を霧で覆う
        fogCanvas.drawColor(FOG_COLOR);

        // ② 通った地点に穴を開ける
        Projection projection = mapView.getProjection();
        Point screenPoint = new Point();
        float radiusPx = projection.metersToPixels((float) RADIUS_METERS);

        for (GeoPoint gp : visitedPoints) {
            projection.toPixels(gp, screenPoint);
            fogCanvas.drawCircle(screenPoint.x, screenPoint.y, radiusPx, clearPaint);
        }

        // ③ 霧レイヤーを地図の上に重ねる
        canvas.drawBitmap(fogBitmap, 0, 0, null);
        fogBitmap.recycle();
    }
}
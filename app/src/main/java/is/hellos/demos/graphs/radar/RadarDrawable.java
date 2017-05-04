package is.hellos.demos.graphs.radar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.models.radar.RadarPoint;
import is.hellos.demos.models.radar.RadarPoints;
import is.hellos.demos.utils.PaintUtil;

public class RadarDrawable extends GraphDrawable {

    private final RadarPoints radarPoints;
    private final Paint paint = PaintUtil.createGraphPaint(255, 155, 155, 155);

    public RadarDrawable(final int width,
                         final int height) {
        super(width, height);
        this.radarPoints = new RadarPoints();
    }

    public void update(@NonNull final RadarPoint radarPoint) {
        this.radarPoints.add(radarPoint);
        invalidateSelf();

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPaint(new Paint());
        for (int i = 0; i < 100; i++) {
/*
            canvas.drawCircle(canvas.getWidth() / 2,
                              canvas.getHeight() / 2,
                              radarPoints.getPoint(i).getX()*10,
                              paint);*/
            //final RadarPoint radarPoint = radarPoints.getPoint(i);
            //canvas.drawLine(radarPoint.getX(), );
        }

    }
}

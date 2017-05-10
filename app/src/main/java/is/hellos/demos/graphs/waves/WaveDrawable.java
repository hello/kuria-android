package is.hellos.demos.graphs.waves;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.Log;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.models.timeline.TimelineItem;
import is.hellos.demos.models.timeline.TimelineTracker;
import is.hellos.demos.models.wave.Wave;
import is.hellos.demos.models.wave.Waves;
import is.hellos.demos.utils.PaintUtil;

public class WaveDrawable extends GraphDrawable {

    private static final float MAX = 5f;
    private final Paint basePaint = PaintUtil.createGraphPaint(175, 155, 155, 155);
    private final Paint feat1Paint = PaintUtil.createGraphPaint(255, 200, 55, 55);
    private final Paint feat2Paint = PaintUtil.createGraphPaint(255, 55, 55, 200);
    private final Paint backgroundPaint = PaintUtil.createGraphPaint(255, 0, 0, 0);
    private final float baseLine;
    private final Waves waves = new Waves();
    private final Paint wavePaint = PaintUtil.createGraphPaint(255, 0, 155, 255);


    public WaveDrawable(final int width,
                        final int height) {
        super(width, height);
        this.baseLine = getIntrinsicHeight() / 2;
        this.feat1Paint.setStrokeWidth(5);
        this.feat1Paint.setStyle(Paint.Style.STROKE);
        this.feat2Paint.setStrokeWidth(5);
        this.feat2Paint.setStyle(Paint.Style.STROKE);
        this.wavePaint.setStrokeWidth(5);
        this.wavePaint.setStyle(Paint.Style.STROKE);
    }

    public void update(final float value1,
                       final float value2) {
        waves.add(value1, value2);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final int size = waves.getSize();
        if (size <= 0) {
            return;
        }
        Wave feat1 = waves.getFeat1Root();
        Wave feat2 = waves.getFeat2Root();
        final float segmentWidth = canvas.getWidth() / size;
        int count = 0;

        Path feat1Path = new Path();
        Path feat2Path = new Path();
        feat1Path.moveTo(0, baseLine);
        feat2Path.moveTo(0, baseLine);
        float height;
        while (feat1 != null && feat2 != null && count < size) {
            height = calculateHeight(feat1.getValue());
            if (height != 0) {
                feat1Path.lineTo(count * segmentWidth, height);
            }else {
               // feat1Path.moveTo(count * segmentWidth, height);
            }
            height = calculateHeight(feat2.getValue());
            if (height != 0) {
                feat2Path.lineTo(count * segmentWidth, height);
            } else {
              //  feat2Path.moveTo(count * segmentWidth, height);
            }
            count++;
            feat1 = feat1.getNext();
            feat2 = feat2.getNext();
        }

        canvas.drawPath(feat1Path, feat1Paint);
        canvas.drawPath(feat2Path, feat2Paint);

    }

    public float calculateHeight(final float value) {
        if (value == 0) {
            return baseLine;
        }
        if (value > 0) {
            return baseLine - ((getIntrinsicHeight() * (value / MAX)) / 2);
        } else {
            return baseLine + ((getIntrinsicHeight() * (Math.abs(value) / MAX)) / 2);

        }
    }

}

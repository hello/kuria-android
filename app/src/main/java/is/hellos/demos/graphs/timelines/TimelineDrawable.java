package is.hellos.demos.graphs.timelines;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.models.timeline.TimelineItem;
import is.hellos.demos.models.timeline.TimelineTracker;
import is.hellos.demos.utils.PaintUtil;

public class TimelineDrawable extends GraphDrawable {

    private TimelineItem root;
    private final Paint paint = PaintUtil.createGraphPaint(255, 155, 155, 155);
    private final Paint pathPaint = PaintUtil.createGraphPaint(255, 200, 155, 155);
    private final Paint backgroundPaint = PaintUtil.createGraphPaint(255, 0, 0, 0);
    private final float baseLine;
    private final float segmentWidth;

    private long lastUpdate = System.currentTimeMillis();

    public TimelineDrawable(final int width,
                            final int height) {
        super(width, height);
        this.baseLine = getIntrinsicHeight() * .8f;
        this.segmentWidth = width / TimelineTracker.LENGTH;
        this.pathPaint.setStrokeWidth(2);
        this.pathPaint.setStyle(Paint.Style.STROKE);
    }

    public void update(@NonNull final TimelineItem root) {
        this.root = root;
        invalidateSelf();

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPaint(backgroundPaint);
        canvas.drawRect(0, baseLine, canvas.getWidth(), baseLine + 5, paint);
        TimelineItem ptr = root;
        int count = 0;
        while (ptr != null) {
            final float startX = count * segmentWidth;
            final float endX = startX + segmentWidth;
            final float startY = baseLine;
            final float endY;
            if (ptr.getItem() != null) {
                endY =baseLine - ptr.getItem().getFirst() * 20;
            } else {
                endY = baseLine;
            }
            final float midX = (startX + endX) /2;
            Path path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(midX, endY);
            path.lineTo(midX, endY);
            path.lineTo(endX, startY);
            canvas.drawPath(path, pathPaint);

            count++;
            ptr = ptr.getNext();
        }

        lastUpdate = System.currentTimeMillis();
    }

}

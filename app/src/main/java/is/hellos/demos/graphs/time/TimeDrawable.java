package is.hellos.demos.graphs.time;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.Pair;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.utils.PaintUtil;


public class TimeDrawable extends GraphDrawable {
    /**
     * Y space this axis should consume.
     */
    private static final float TIME_AXIS_HEIGHT_RATIO = .05f;

    /**
     * Number of seconds to display into the past.
     */
    private static final int NUM_OF_SEC = 10;


    private final int framesPerSecond;
    private final float segmentWidth;
    private final float columnWidth;
    private final float deltaMS;
    private final float baseLine;
    private final int numberOfFrames;
    private TimeAxis timeAxis;

    private final Paint columnPaint = PaintUtil.createGraphPaint(70, 255, 0, 155);
    private final Path baseLinePath = new Path();
    private final Paint[] paints = new Paint[]{
            PaintUtil.createDashedPathPaint(0),
            PaintUtil.createDashedPathPaint(10),
            PaintUtil.createDashedPathPaint(20),
            PaintUtil.createDashedPathPaint(30),
            PaintUtil.createDashedPathPaint(40),
            PaintUtil.createDashedPathPaint(50),
            PaintUtil.createDashedPathPaint(60)
    };
    private int paintPointer = 0;
    private int numOfDraws = 0;


    public TimeDrawable(final int width,
                        final int height,
                        final int framesPerSecond) {
        super(width, height);
        this.framesPerSecond = framesPerSecond;
        this.deltaMS = 1000 / framesPerSecond;
        this.baseLine = (getIntrinsicHeight() - (getIntrinsicHeight() * TIME_AXIS_HEIGHT_RATIO)) / 2;
        this.baseLinePath.moveTo(0, this.baseLine);
        this.baseLinePath.lineTo(width, this.baseLine);
        this.numberOfFrames = framesPerSecond * NUM_OF_SEC;
        this.columnWidth = getIntrinsicWidth() / NUM_OF_SEC;
        this.segmentWidth = getIntrinsicWidth() / this.numberOfFrames;
        this.timeAxis = new TimeAxis();


    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPath(baseLinePath, paints[paintPointer]);
        incrementPaintPointer();
        for (int i = 0; i < NUM_OF_SEC; i++) {
            float startX = (i * columnWidth);
            float endX = startX + 1;
            float startY = 0;
            float endY = getIntrinsicHeight();
            //     canvas.drawRect(startX, startY, endX, endY, columnPaint);
        }
        timeAxis.draw(canvas);
    }


    private void incrementPaintPointer() {
        if (numOfDraws >= framesPerSecond) {
            numOfDraws = 0;
        } else {
            numOfDraws++;
            return;
        }
        if (paintPointer + 1 >= paints.length) {
            paintPointer = 0;
        } else {
            paintPointer += 1;
        }
    }


    /**
     * @param second should be a value greater than 0 and equal to {@link TimeDrawable#NUM_OF_SEC}
     * @return Rect of on time axis.
     */
    private Rect getPositionOnTimeAxisForSecond(final int second) {
        if (second < 1 || second > NUM_OF_SEC) {
            throw new IllegalStateException("invalid second requested");
        }
        final Rect rect = new Rect();
        rect.left = (int) ((NUM_OF_SEC - second) * columnWidth);
        rect.right = (int) (rect.left + columnWidth);
        rect.top = (int) (getIntrinsicHeight() - (getIntrinsicHeight() * TIME_AXIS_HEIGHT_RATIO));
        rect.bottom = getIntrinsicHeight();
        return rect;

    }

    private class TimeAxis {
        private final TimeAxisCol past = new TimeAxisCol(NUM_OF_SEC + "s", NUM_OF_SEC);
        private final TimeAxisCol now = new TimeAxisCol("Now", 1);


        public void draw(@NonNull Canvas canvas) {
            past.draw(canvas);
            now.draw(canvas);

        }

        private class TimeAxisCol {
            private final TextPaint textPaint = PaintUtil.createTextPaint(150, 55, 55, 55);
            private final String text;
            private final Rect rect;
            private final Pair<Float, Float> coords;

            public TimeAxisCol(@NonNull final String text,
                               final int second) {
                this.text = text;
                this.rect = getPositionOnTimeAxisForSecond(second);
                PaintUtil.getCorrectTextSize(textPaint, text, rect.width(), rect.height(), 60);
                this.coords = PaintUtil.getCoordsForText(rect, textPaint, text);

            }

            public void draw(@NonNull Canvas canvas) {
                canvas.drawText(this.text,
                                0,
                                this.text.length(),
                                this.coords.first,
                                this.coords.second,
                                this.textPaint);
            }
        }
    }
}

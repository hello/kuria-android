package is.hellos.demos.graphs.time;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.Log;
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
    private static final float NUM_OF_SEC = 10f;

    private static final float NUM_OF_MSEC = (NUM_OF_SEC) * 1000f;

    /**
     * Responsible for drawing anything on the time axis.
     */
    private TimeAxis timeAxis;

    private final int framesPerSecond;
    private final float columnWidth;
    private final float baseLine;
    private float maxValue = 0;

    private final Path baseLinePath = new Path();
    private final Paint[] paints = new Paint[10];
    private int paintPointer = 0;
    private final NodeController feat1;
    private final NodeController feat2;

    private final Path path = new Path();

    public TimeDrawable(final int width,
                        final int height,
                        final int framesPerSecond) {
        super(width, height);
        for (int i = 0; i < paints.length; i++) {
            paints[i] = PaintUtil.createDashedPathPaint(i * 6);

        }
        this.framesPerSecond = framesPerSecond;
        this.baseLine = (getIntrinsicHeight() - (getIntrinsicHeight() * TIME_AXIS_HEIGHT_RATIO)) / 2;
        this.baseLinePath.moveTo(0, this.baseLine);
        this.baseLinePath.lineTo(width, this.baseLine);
        this.columnWidth = getIntrinsicWidth() / NUM_OF_SEC;
        this.timeAxis = new TimeAxis();
        this.feat1 = new NodeController(PaintUtil.getPathPaint(220, 200, 30, 20));
        this.feat2 = new NodeController(PaintUtil.getPathPaint(200, 20, 50, 170));


    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPath(baseLinePath, paints[paintPointer]);
        incrementPaintPointer();
        feat1.draw(canvas);
        feat2.draw(canvas);
    }

    void addNode(final long time,
                 final float feat1,
                 final float feat2) {
        this.feat1.addNode(time, feat1);
        this.feat2.addNode(time, feat2);
    }


    private void incrementPaintPointer() {
        if (true) {
            return;
        }
        if (paintPointer + 1 >= paints.length) {
            paintPointer = 0;
        } else {
            paintPointer += 1;
        }
    }

    private void setMaxValue(final float value) {
        this.maxValue = Math.abs(value);
    }

    private boolean isGreaterThanMaxValue(final float value) {
        return Math.abs(value) > maxValue;
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

    private float getHeightFor(final float value) {
        if (isGreaterThanMaxValue(value)) {
            return -1;
        }
        if (value == 0) {
            return baseLine;
        }
        final float ratio = (Math.abs(value) % maxValue) / maxValue;
        if (ratio > 1) {
            return -1;
        }
        if (value > 0) {
            return baseLine - (getIntrinsicHeight() / 2 * ratio);
        } else {
            return baseLine + (getIntrinsicHeight() / 2 * ratio);
        }
    }

    private class TimeAxis {
        private final TimeAxisCol past = new TimeAxisCol(NUM_OF_SEC + "s", (int) NUM_OF_SEC);
        private final TimeAxisCol now = new TimeAxisCol("Now", 1);


        private void draw(@NonNull Canvas canvas) {
            past.draw(canvas);
            now.draw(canvas);

        }

        private class TimeAxisCol {
            private final TextPaint textPaint = PaintUtil.createTextPaint(150, 55, 55, 55);
            private final String text;
            private final Rect rect;
            private final Pair<Float, Float> coords;

            private TimeAxisCol(@NonNull final String text,
                                final int second) {
                this.text = text;
                this.rect = getPositionOnTimeAxisForSecond(second);
                PaintUtil.getCorrectTextSize(textPaint, text, rect.width(), rect.height(), 60);
                this.coords = PaintUtil.getCoordsForText(rect, textPaint, text);

            }

            private void draw(@NonNull Canvas canvas) {
                canvas.drawText(this.text,
                                0,
                                this.text.length(),
                                this.coords.first,
                                this.coords.second,
                                this.textPaint);
            }
        }

    }

    private class NodeController {
        private final Paint pathPaint;
        private Node root;
        private Node tail;
        private int numOfNodes = 0;

        private NodeController(Paint paint) {
            this.pathPaint = paint;
            initialize();
        }

        private synchronized void initialize() {
            if (root == null || numOfNodes == 0) {
                root = new Node(System.currentTimeMillis(), 10);
                tail = root;
                numOfNodes = 1;
            }
        }

        private synchronized void addNode(final long time,
                                          final float value) {
            if (Math.abs(value) > 2) {
                return;
            }
            if (isGreaterThanMaxValue(value)) {
                setMaxValue(value);
            }
            this.tail.next = new Node(time, value);
            this.tail = tail.next;
            numOfNodes += 1;

            boolean findMaxValue = false;
            final long currentTime = System.currentTimeMillis();
            final long minTime = currentTime - (long) NUM_OF_MSEC;
            while (root != null && root.segment < minTime) {
                if (isGreaterThanMaxValue(root.value)) {
                    findMaxValue = true;
                    setMaxValue(0);
                    Log.e(getClass().getSimpleName(), "Find new max value");
                }
                root = root.next;
                numOfNodes--;
            }
            initialize();
            if (findMaxValue) {
                Node ptr = root;
                while (ptr != null) {
                    if (isGreaterThanMaxValue(ptr.value)) {
                        setMaxValue(ptr.value);
                        Log.e(getClass().getSimpleName(), "max value: " + maxValue);

                    }
                    ptr = ptr.next;
                }
            }
        }

        private void draw(@NonNull final Canvas canvas) {
            final long currentTime = System.currentTimeMillis();
            Node ptr = root;
            timeAxis.draw(canvas);
            path.reset();
            path.moveTo(0, baseLine);
            while (ptr != null) {
                final Pair<Float, Float> position = ptr.getCoords(currentTime);
                if (position != null) {
                    path.moveTo(position.first, position.second);
                    ptr = ptr.next;
                    break;
                }
                ptr = ptr.next;
            }
            while (ptr != null) {
                final Pair<Float, Float> position = ptr.getCoords(currentTime);
                if (position != null) {
                    path.lineTo(position.first, position.second);
                }
                ptr = ptr.next;
            }
            canvas.drawPath(path, pathPaint);

        }
    }

    private class Node {
        private final long segment;
        private final float value;
        private Node next;

        public Node(final long segment,
                    final float value) {
            this.segment = segment;
            this.value = value;
        }


        @Nullable
        public Pair<Float, Float> getCoords(final long currentTime) {
            final long dTime = currentTime - segment;
            if (dTime <= NUM_OF_MSEC) {
                final float timeRatio = (dTime / NUM_OF_MSEC);
                final float x = getIntrinsicWidth() - (getIntrinsicWidth() * timeRatio);
                final float y = getHeightFor(value);
                if (y == -1) {
                    return null;
                }
                return new Pair<>(x, y);
            }
            return null;
        }

    }

}

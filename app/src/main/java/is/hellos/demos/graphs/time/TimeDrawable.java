package is.hellos.demos.graphs.time;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
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
    private static final int NUM_OF_SEC = 10;

    private static final long TIME_IN_MS = (NUM_OF_SEC + 1) * 1000;
    /**
     * Responsible for drawing anything on the time axis.
     */
    private TimeAxis timeAxis;

    private final int framesPerSecond;
    private final float frameWidth;
    private final float columnWidth;
    private final long deltaMS;
    private final float baseLine;
    private final int numberOfFrames;
    private final long startTime;

    private final Paint columnPaint = PaintUtil.createGraphPaint(70, 255, 0, 155);
    private final Path baseLinePath = new Path();
    private final Paint[] paints = new Paint[11];/* = new Paint[]{
            PaintUtil.createDashedPathPaint(0),
            PaintUtil.createDashedPathPaint(10),
            PaintUtil.createDashedPathPaint(20),
            PaintUtil.createDashedPathPaint(30),
            PaintUtil.createDashedPathPaint(40),
            PaintUtil.createDashedPathPaint(50),
            PaintUtil.createDashedPathPaint(60)
    };*/
    private int paintPointer = 0;
    private int numOfDraws = 0;
    private final NodeController nodeController;


    public TimeDrawable(final int width,
                        final int height,
                        final int framesPerSecond) {
        super(width, height);
        this.startTime = System.currentTimeMillis();
        for (int i = 0; i < paints.length; i++) {
            paints[i] = PaintUtil.createDashedPathPaint(i*6);

        }
        this.framesPerSecond = framesPerSecond;
        this.deltaMS = 1000 / framesPerSecond;
        this.baseLine = (getIntrinsicHeight() - (getIntrinsicHeight() * TIME_AXIS_HEIGHT_RATIO)) / 2;
        this.baseLinePath.moveTo(0, this.baseLine);
        this.baseLinePath.lineTo(width, this.baseLine);
        this.numberOfFrames = framesPerSecond * NUM_OF_SEC;
        this.columnWidth = getIntrinsicWidth() / NUM_OF_SEC;
        this.frameWidth = getIntrinsicWidth() / numberOfFrames;
        this.timeAxis = new TimeAxis();
        this.nodeController = new NodeController();


    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPath(baseLinePath, paints[paintPointer]);
        incrementPaintPointer();
        final long currentTime = System.currentTimeMillis();
        Node ptr = nodeController.root;
        timeAxis.draw(canvas);
        while (ptr != null) {
            final long dTime = currentTime - ptr.segment;
            if (dTime <= TIME_IN_MS) {
                final float xStart = getIntrinsicWidth() - ((dTime / framesPerSecond) * frameWidth);
                final float xEnd = xStart + frameWidth;
                final float yStart = ptr.value == 0 ? baseLine : ptr.value;
                final float yEnd = baseLine;
                //Log.e("Drawing", "[ " + xStart + ", " + yStart + ", " + xEnd + ", " + yEnd + " ]");
                canvas.drawRect(xStart, yStart, xEnd, yEnd, columnPaint);
            }
            ptr = ptr.next;
        }

    }

    void addNode(final long time,
                 final float value) {
        this.nodeController.addNode(time, value);
    }


    private void incrementPaintPointer() {
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
        private Node root;
        private Node tail;
        private int numOfNodes = 0;

        private NodeController() {
            initialize();
        }

        private void initialize() {
            if (root == null) {
                root = new Node(System.currentTimeMillis(), 10);
                tail = root;
                numOfNodes = 1;
            }
        }

        private synchronized void addNode(final long time,
                                          final float value) {
            this.tail.next = new Node(time, value);
            this.tail = tail.next;
            numOfNodes += 1;

            final long minTime = System.currentTimeMillis() - TIME_IN_MS;
            while (root != null && root.segment < minTime) {
                root = root.next;
                numOfNodes--;
            }
            initialize();
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
    }
}

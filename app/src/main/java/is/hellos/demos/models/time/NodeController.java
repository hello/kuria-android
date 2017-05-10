package is.hellos.demos.models.time;


import android.graphics.Paint;
import android.support.annotation.NonNull;

public class NodeController {
    private final Paint pathPaint;
    private Node root;
    private Node tail;
    private int numOfNodes = 0;
    private MaxTracker maxTracker;
    private final float numberOfMilliSeconds;

    public NodeController(final float numberOfMilliSeconds,
                          @NonNull final Paint paint,
                          @NonNull final MaxTracker maxTracker) {
        this.numberOfMilliSeconds = numberOfMilliSeconds;
        this.pathPaint = paint;
        this.maxTracker = maxTracker;
        initialize();
    }

    private synchronized void initialize() {
        if (root == null || numOfNodes == 0) {
            root = new Node(System.currentTimeMillis(), 10);
            tail = root;
            numOfNodes = 1;
        }
    }

    public synchronized void addNode(final long time,
                                     final float value) {
        if (Math.abs(value) > 2) {
            return;
        }
        if (maxTracker.isGreaterThanMaxValue(value)) {
            maxTracker.setMaxValue(value);
        }
        this.tail.setNext(new Node(time, value));
        this.tail = tail.getNext();
        numOfNodes += 1;

        boolean findMaxValue = false;
        final long currentTime = System.currentTimeMillis();
        final long minTime = currentTime - (long) numberOfMilliSeconds;
        while (root != null && root.getSegment() < minTime) {
            if (maxTracker.isGreaterThanMaxValue(root.getValue())) {
                findMaxValue = true;
                maxTracker.setMaxValue(0);
            }
            root = root.getNext();
            numOfNodes--;
        }
        initialize();
        if (findMaxValue) {
            Node ptr = root;
            while (ptr != null) {
                if (maxTracker.isGreaterThanMaxValue(ptr.getValue())) {
                    maxTracker.setMaxValue(ptr.getValue());
                }
                ptr = ptr.getNext();
            }
        }
    }

    public Node getRoot() {
        return root;
    }

    public Paint getPathPaint() {
        return pathPaint;
    }

    public interface MaxTracker {
        boolean isGreaterThanMaxValue(final float value);

        void setMaxValue(final float value);

    }
}
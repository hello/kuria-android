package is.hellos.demos.models.time;


import android.graphics.Paint;
import android.support.annotation.NonNull;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class NodeController {
    private static final int BACK_NODES_CAPACITY = 20;
    private Queue<Node> backNodes = new LinkedBlockingQueue<>(BACK_NODES_CAPACITY);
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
            // return;
        }
        final float averageValue = backNodesAverage(value);
        if (maxTracker.isGreaterThanMaxValue(averageValue)) {
            maxTracker.setMaxValue(averageValue);
        }
        final Node node = new Node(time, averageValue);
        if (backNodes.size() == BACK_NODES_CAPACITY){
            backNodes.remove();
        }
        backNodes.add(node);
        this.tail.setNext(node);
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

    private float backNodesAverage(final float value) {
        float average = 0;
        int count = 1;
        for (final Node node : backNodes) {
            average += node.getValue();
            count++;
        }
        average += value;
        average /= count;
        return average;
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
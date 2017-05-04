package is.hellos.demos.models.wave;

public class WaveList {

    private static final int SEGMENTS_PER_SECOND = 20;
    private static final int SECONDS_TO_TRACK = 5;

    private static final int LENGTH = SEGMENTS_PER_SECOND * SECONDS_TO_TRACK;


    private int size = 1;
    private Wave root = new Wave(0);
    private Wave tail = root;


    public synchronized void add(final float value) {
        tail = tail.addNext(value);
        if (size >= LENGTH) {
            root = root.getNext();
        } else {
            size += 1;
        }
    }

    public Wave getRoot() {
        return root;
    }

    public int getSize() {
        return size;
    }
}
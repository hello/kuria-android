package is.hellos.demos.models.time;


public class Node {
    private final long segment;
    private final float value;
    private Node next;

    public Node(final long segment,
                final float value) {
        this.segment = segment;
        this.value = value;
    }


    public long getSegment() {
        return segment;
    }

    public float getValue() {
        return value;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}

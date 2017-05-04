package is.hellos.demos.models.timeline;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TimelineTracker {

    public static final int LENGTH = 50;
    @NonNull
    private TimelineItem root;
    @NonNull
    private TimelineItem tail;
    private int size;

    TimelineTracker(final long time) {
        this.root = new TimelineItem(time, null);
        this.tail = root;
        this.size = 1;
    }

    void add(final long time,
             @Nullable final Item timeItem) {
        tail.setNext(new TimelineItem(time, timeItem));
        tail = tail.getNext();
        if (size == LENGTH) {
            root = root.getNext();
        } else {
            size += 1;
        }

    }

    @NonNull
    public TimelineItem getRoot() {
        return root;
    }
}

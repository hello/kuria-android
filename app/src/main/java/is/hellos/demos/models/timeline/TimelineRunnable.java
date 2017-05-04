package is.hellos.demos.models.timeline;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class TimelineRunnable implements Runnable {
    private static final long MIN_ELAPSED_TIME = 500; //ms

    private final TimelineTracker graphTimeTracker = new TimelineTracker(System.currentTimeMillis());
    private long lastUpdate = 0; //ms
    private boolean running = true;
    @NonNull
    private Listener listener = EMPTY_LISTENER;

    @Override
    public void run() {
        while (running) {
            if (System.currentTimeMillis() - lastUpdate > MIN_ELAPSED_TIME) {
                lastUpdate = System.currentTimeMillis();
                addGraphTime(lastUpdate, null);
            }
        }
    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public void setListener(@Nullable final Listener listener) {
        if (listener == null) {
            this.listener = EMPTY_LISTENER;
            return;
        }
        this.listener = listener;
    }

    public void addGraphTime(@Nullable final Item timeItem) {
        this.addGraphTime(System.currentTimeMillis(), timeItem);
    }

    private void addGraphTime(final long time,
                              @Nullable final Item timeItem) {
        this.graphTimeTracker.add(time, timeItem);
        this.listener.onUpdated();

    }

    public TimelineTracker getGraphTimeTracker() {
        return graphTimeTracker;
    }

    private static final Listener EMPTY_LISTENER = new Listener() {
        @Override
        public void onUpdated() {

        }
    };

    public interface Listener {
        void onUpdated();
    }

}

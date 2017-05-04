package is.hellos.demos.models.timeline;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimelineItem {
    private TimelineItem next = null;
    private final long timeInMilli;
    private final String formattedTime;
    @Nullable
    private final Item item;

    public TimelineItem(final long timeInMilli,
                        @Nullable final Item item) {
        this.timeInMilli = timeInMilli;
        this.item = item;
        this.formattedTime = format();
    }

    private String format() {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                             TimeUnit.MILLISECONDS.toHours(timeInMilli),
                             TimeUnit.MILLISECONDS.toMinutes(timeInMilli) -
                                     TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMilli)),
                             TimeUnit.MILLISECONDS.toSeconds(timeInMilli) -
                                     TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMilli)));
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public long getTimeInMilli() {
        return timeInMilli;
    }

    @Nullable
    public Item getItem() {
        return item;
    }

    public TimelineItem getNext() {
        return next;
    }

    public void setNext(@NonNull final TimelineItem next) {
        this.next = next;
    }
}

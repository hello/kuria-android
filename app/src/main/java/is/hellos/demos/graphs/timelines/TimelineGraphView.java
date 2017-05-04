package is.hellos.demos.graphs.timelines;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.graphs.GraphView;
import is.hellos.demos.models.timeline.TimelineItem;

public class TimelineGraphView extends GraphView {
    public TimelineGraphView(final Context context) {
        super(context);
    }

    public TimelineGraphView(final Context context,
                             @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public TimelineGraphView(final Context context,
                             @Nullable final AttributeSet attrs,
                             final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRoot(@NonNull final TimelineItem timelineItem) {
        if (getBackground() == null){
            return;
        }
        ((TimelineDrawable) getBackground()).update(timelineItem);
        redraw();

    }


    @Override
    public GraphDrawable getGraphDrawable() {
        return new TimelineDrawable(getWidth(), getHeight());
    }
}

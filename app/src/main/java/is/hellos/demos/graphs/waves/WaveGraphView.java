package is.hellos.demos.graphs.waves;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.graphs.GraphView;
import is.hellos.demos.graphs.timelines.TimelineDrawable;
import is.hellos.demos.models.timeline.TimelineItem;

public class WaveGraphView extends GraphView {
    private static final int FPS = 30;
    private static final long DELTA_MS = 1000 / FPS;

    private long lastUpdate = 0;

    public WaveGraphView(final Context context) {
        super(context);
    }

    public WaveGraphView(final Context context,
                         @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveGraphView(final Context context,
                         @Nullable final AttributeSet attrs,
                         final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void update(final float value1,
                       final float value2) {
        if (getBackground() == null) {
            return;
        }
        ((WaveDrawable) getBackground()).update(value1, value2);
        lastUpdate = System.currentTimeMillis();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (System.currentTimeMillis() - lastUpdate > DELTA_MS) {
            ((WaveDrawable) getBackground()).update(0, 0);
        }
        postInvalidateDelayed(DELTA_MS);
    }

    @Override
    public GraphDrawable getGraphDrawable() {
        return new WaveDrawable(getWidth(), getHeight());
    }
}

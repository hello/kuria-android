package is.hellos.demos.graphs.time;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.graphs.GraphView;

public class TimeGraphView extends GraphView {
    private static final int FPS = 30;
    private static final long DELTA_MS = 1000 / FPS;
    private static final long DELTA_MSX2 = DELTA_MS *2;
    private static long lastUpdate = 0;

    public TimeGraphView(final Context context) {
        super(context);
    }

    public TimeGraphView(final Context context,
                         @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeGraphView(final Context context,
                         @Nullable final AttributeSet attrs,
                         final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final long currentTime =System.currentTimeMillis();
        if (currentTime - lastUpdate >=DELTA_MSX2 ){
            addValue(0,0);
        }
        postInvalidateDelayed(DELTA_MS);
    }

    @Override
    public GraphDrawable getGraphDrawable() {
        return new TimeDrawable(getWidth(), getHeight());
    }

    public void addValue(final float feat1,
                         final float feat2) {
        lastUpdate = System.currentTimeMillis();
        ((TimeDrawable) getBackground()).addNode(System.currentTimeMillis(), feat1, feat2);
    }

}

package is.hellos.demos.graphs.time;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.graphs.GraphView;

public class TimeGraphView extends GraphView{
    private static final int FPS = 30;
    private static final long DELTA_MS = 1000 / FPS;

    private long lastUpdate = 0;
    public int updates = 0;

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
        if (System.currentTimeMillis() - lastUpdate > DELTA_MS) {
            if (updates > 10){
                updates = 0;
            }else {
            }
            updates++;
        }
        postInvalidateDelayed(DELTA_MS);
    }

    @Override
    public GraphDrawable getGraphDrawable() {
        return new TimeDrawable(getWidth(), getHeight(), FPS);
    }


}

package is.hellos.demos.graphs.radar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.graphs.GraphView;
import is.hellos.demos.models.radar.RadarPoint;

public class RadarGraphView extends GraphView {

    public RadarGraphView(final Context context) {
        super(context);
    }

    public RadarGraphView(final Context context,
                          @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public RadarGraphView(final Context context,
                          @Nullable final AttributeSet attrs,
                          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addRadarPoint(@NonNull final RadarPoint radarPoint) {
        ((RadarDrawable)getBackground()).update(radarPoint);
        requestLayout();

    }


    @Override
    public GraphDrawable getGraphDrawable() {
        return new RadarDrawable(getWidth(), getHeight());
    }
}

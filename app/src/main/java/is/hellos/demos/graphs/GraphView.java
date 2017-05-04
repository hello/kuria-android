package is.hellos.demos.graphs;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public abstract class GraphView extends View {

    public GraphView(final Context context) {
        this(context, null);
    }

    public GraphView(final Context context,
                     @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraphView(final Context context,
                     @Nullable final AttributeSet attrs,
                     final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        post(new Runnable() {
            @Override
            public void run() {
                recreateDrawable();
            }
        });
    }

    public void redraw() {
        getBackground().invalidateSelf();
        requestLayout();
    }

    public void recreateDrawable() {
        setBackground(getGraphDrawable());
    }

    public abstract GraphDrawable getGraphDrawable();
}

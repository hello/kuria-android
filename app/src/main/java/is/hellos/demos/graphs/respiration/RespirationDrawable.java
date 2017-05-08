package is.hellos.demos.graphs.respiration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import is.hellos.demos.graphs.GraphDrawable;

/**
 * Created by simonchen on 5/8/17.
 */

public class RespirationDrawable extends GraphDrawable {

    private final float initialRadius;
    private float radius;
    private Paint innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint expandingCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RespirationDrawable(int width, int height, float radius) {
        super(width, height);
        this.initialRadius = radius;
        this.radius = radius;
        this.innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.expandingCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, radius, expandingCirclePaint);
        canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, initialRadius, innerCirclePaint);
    }

    public void setRadius(final float radius) {
        this.radius = radius;
    }

    public void setInnerCircleColor(@ColorInt int color) {
        this.innerCirclePaint.setColor(color);
    }

    public void setExpandingCircleColor(@ColorInt int color) {
        this.expandingCirclePaint.setColor(color);
    }
}

package is.hellos.demos.graphs.respiration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.utils.PaintUtil;

/**
 * Created by simonchen on 5/8/17.
 */

public class RespirationDrawable extends GraphDrawable {

    private final float initialRadius;
    private float radius;
    private Paint innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint expandingCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint breathRateTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private String breathRateText;
    private int innerCircleColor;

    public RespirationDrawable(int width, int height, float radius, String initialBreathRate) {
        super(width, height);
        this.initialRadius = radius;
        this.radius = radius;
        this.innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.expandingCirclePaint.setStyle(Paint.Style.FILL);
        this.breathRateTextPaint.setColor(Color.WHITE);
        this.breathRateText = initialBreathRate;
        PaintUtil.getCorrectTextSize(breathRateTextPaint,
                breathRateText,
                (int) initialRadius * 2,
                (int) initialRadius * 2,
                100); //maxTextSize
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        final float centerX = canvas.getWidth()/2;
        final float centerY = canvas.getHeight()/2;
        canvas.drawCircle(centerX, centerY, radius, expandingCirclePaint);
        canvas.drawCircle(centerX, centerY, initialRadius, innerCirclePaint);
        PaintUtil.drawAndCenterText(canvas, breathRateTextPaint, breathRateText);
    }

    public void setBreathRateText(final String breathRateText) {
        this.breathRateText = breathRateText;
    }

    public void setRadius(final float radius) {
        this.radius = radius;
    }

    public void setInnerCircleColor(@ColorInt int color) {
        this.innerCirclePaint.setColor(color);
    }

    @ColorInt
    public int getInnerCircleColor() {
        return this.innerCirclePaint.getColor();
    }

    public void setExpandingCircleColor(@ColorInt int color) {
        this.expandingCirclePaint.setColor(color);
    }
}

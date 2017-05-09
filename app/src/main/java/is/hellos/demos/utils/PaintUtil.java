package is.hellos.demos.utils;


import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.Pair;

public class PaintUtil {

    /**
     * @param a The new alpha component (0..255) of the paint's color.
     * @param r The new red component (0..255) of the paint's color.
     * @param g The new green component (0..255) of the paint's color.
     * @param b The new blue component (0..255) of the paint's color.
     * @return
     */
    public static TextPaint createTextPaint(int a, int r, int g, int b) {
        final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(a, r, g, b);
        return paint;
    }

    /**
     * @param a The new alpha component (0..255) of the paint's color.
     * @param r The new red component (0..255) of the paint's color.
     * @param g The new green component (0..255) of the paint's color.
     * @param b The new blue component (0..255) of the paint's color.
     * @return
     */
    public static Paint createGraphPaint(int a, int r, int g, int b) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(a, r, g, b);
        return paint;
    }

    public static Paint createDashedPathPaint(final int a,
                                              final int r,
                                              final int g,
                                              final int b,
                                              final int stroke,
                                              final int dashSize,
                                              final int dashDistance,
                                              final int phase) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(a, r, g, b);
        paint.setStrokeWidth(stroke);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{dashSize, dashDistance}, phase));
        return paint;
    }

    public static Paint createDashedPathPaint(
            final int stroke,
            final int dashSize,
            final int dashDistance,
            final int phase) {
        return createDashedPathPaint(255, 50, 50, 50, stroke, dashSize, dashDistance, phase);
    }

    public static Paint createDashedPathPaint(final int phase) {
        return createDashedPathPaint(2, 30, 30, phase);
    }


    public static void getCorrectTextSize(@NonNull final TextPaint paint,
                                          @Nullable final String text,
                                          final int maxWidth,
                                          final int maxHeight,
                                          final int maxTextSize) {
        if (text == null || text.isEmpty()) {
            return;
        }
        paint.setTextSize(0);
        final Rect textBounds = new Rect();
        while (doesTextFit(paint, text, textBounds, maxWidth, maxHeight)) {
            if (maxTextSize != -1 && maxTextSize <= paint.getTextSize()) {
                return;
            }
            paint.setTextSize(paint.getTextSize() + 1);
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean doesTextFit(@NonNull final TextPaint paint,
                                       @NonNull final String text,
                                       @NonNull final Rect textBounds,
                                       final int width,
                                       final int height) {
        paint.getTextBounds(text, 0, text.length(), textBounds);
        if (textBounds.height() > height) {
            return false;
        }
        if (textBounds.width() > width) {
            return false;
        }
        return true;
    }

    public static Pair<Float, Float> getCoordsForText(@NonNull final Rect rect,
                                                      @NonNull final Paint paint,
                                                      @NonNull final String text) {
        final Rect drawArea = new Rect();
        paint.getTextBounds(text, 0, text.length(), drawArea);
        final float cHeight = drawArea.height();
        final float cWidth = drawArea.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), drawArea);
        final float x = cWidth / 2f - drawArea.width() / 2f - drawArea.left;
        final float y = cHeight / 2f + drawArea.height() / 2f - drawArea.bottom;
        final float dx;
        if (rect.width() > drawArea.width()) {
            dx = (rect.width() - drawArea.width()) / 2;
        } else {
            dx = 0;
        }
        final float dy;
        if (rect.height() > drawArea.height()) {
            dy = (rect.height() - drawArea.height()) / 2;
        } else {
            dy = 0;
        }
        return new Pair<>(rect.left + x + dx, rect.top + y + dy);
    }

    public static void drawAndCenterText(@NonNull final Canvas canvas,
                                         @NonNull final Paint paint,
                                         @NonNull final String text) {
        final Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.getClipBounds(textBounds);
        final float cHeight = textBounds.height();
        final float cWidth = textBounds.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), textBounds);
        final float x = cWidth / 2f - textBounds.width() / 2f - textBounds.left;
        final float y = cHeight / 2f + textBounds.height() / 2f - textBounds.bottom;
        canvas.drawText(text, x, y, paint);
    }
    public static Paint getPathPaint(int a, int r, int g, int b) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(a, r, g, b);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }
}

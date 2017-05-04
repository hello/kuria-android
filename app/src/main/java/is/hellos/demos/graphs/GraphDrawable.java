package is.hellos.demos.graphs;


import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class GraphDrawable extends Drawable {
    private int height;
    private int width;

    public GraphDrawable(final int width,
                         final int height) {
        this.height = height;
        this.width = width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }


    /**
     * Something to remember is the canvas is drawn upside down. The highest point of the graph is 0
     * and the lowest point of the graph is equal to the canvas height. So the smaller y is, the taller
     * the graph is.
     * <p>
     * (0,0)                           (canvas.getWidth(), 0)
     * ______________________________
     * |                _____        |
     * |  __/\    /\___/     \      _|
     * |_/    \__/            \____/ |
     * |_____________________________|
     * (0, canvas.getHeight())         (canvas.getWidth(), canvas.getHeight())
     */
    public abstract void draw(@NonNull Canvas canvas);
}

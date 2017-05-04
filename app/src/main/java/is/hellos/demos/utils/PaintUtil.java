package is.hellos.demos.utils;


import android.graphics.Paint;

public class PaintUtil {

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
}

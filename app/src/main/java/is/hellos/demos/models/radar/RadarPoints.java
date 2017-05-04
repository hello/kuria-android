package is.hellos.demos.models.radar;


import android.support.annotation.NonNull;

public class RadarPoints {
    private final static int MAX_SIZE = 100;
    private final RadarPoint[] points = new RadarPoint[MAX_SIZE];
    private int pointer = 0;

    public RadarPoints() {
        for (int i = 0; i < MAX_SIZE; i++) {
            points[i] = RadarPoint.getEmptyPoint();
        }
    }

    public void add(@NonNull final RadarPoint radarPoint) {
        points[pointer] = radarPoint;
        if (pointer + 1 == MAX_SIZE) {
            pointer = 0;
        } else {
            pointer += 1;
        }
    }

    @NonNull
    public RadarPoint getPoint(final int i) {
        // confirm i is safe.
        return points[i];
    }

}

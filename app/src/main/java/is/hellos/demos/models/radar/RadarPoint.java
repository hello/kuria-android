package is.hellos.demos.models.radar;

public class RadarPoint {

    private final float x;
    private final float y;

    public RadarPoint(final float x,
                      final float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static RadarPoint getEmptyPoint() {
        return new EmptyPoint();
    }

    private static class EmptyPoint extends RadarPoint {
        public EmptyPoint() {
            super(0, 0);
        }
    }
}

package is.hellos.demos.models.wave;


public class Waves {
    public static final int SEGMENTS_PER_SECOND = 20;
    public static final int SECONDS_TO_TRACK = 10;

    public final WaveList feat1 = new WaveList();
    public final WaveList feat2 = new WaveList();

    public Wave getFeat1Root() {
        return feat1.getRoot();
    }

    public Wave getFeat2Root() {
        return feat2.getRoot();
    }


    public void add(final float value1, final float value2) {
        this.feat1.add(value1);
        this.feat2.add(value2);
    }


    public int getSize() {
        return Math.min(feat1.getSize(), feat2.getSize());
    }
}

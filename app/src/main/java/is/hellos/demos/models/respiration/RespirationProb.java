package is.hellos.demos.models.respiration;

/**
 * Created by simonchen on 5/10/17.
 */

public class RespirationProb {

    private final float exhaled;
    private final float inhaling;
    private final float inhaled;
    private final float exhaling;


    public RespirationProb(float exhaled, float inhaling, float inhaled, float exhaling) {
        this.exhaled = exhaled;
        this.inhaling = inhaling;
        this.inhaled = inhaled;
        this.exhaling = exhaling;
    }

    public float getExhaled() {
        return exhaled;
    }

    public float getInhaling() {
        return inhaling;
    }

    public float getInhaled() {
        return inhaled;
    }

    public float getExhaling() {
        return exhaling;
    }

    @Override
    public String toString() {
        return "RespirationProb{" +
                "\nexhaled=" +
                exhaled +
                ",\nexhaling=" +
                exhaling +
                ",\ninhaled=" +
                inhaled +
                ",\ninhaling=" +
                inhaling +
                "}";
    }
}

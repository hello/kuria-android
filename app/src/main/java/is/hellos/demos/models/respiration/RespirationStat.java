package is.hellos.demos.models.respiration;

import android.support.annotation.NonNull;

import is.hellos.demos.models.protos.RadarMessages;

/**
 * Created by simonchen on 5/8/17.
 */

public class RespirationStat {

    private final static String ID = "respiration";
    private final float breathDurationSeconds;
    private final float standardDevBPM;
    private final float energyDb;
    private final boolean hasRespiration;

    public static RespirationStat convertFrom(@NonNull final RadarMessages.FeatureVector featureVector) {
        if (ID.equals(featureVector.getId())) {
            return new RespirationStat(
                    featureVector.getFloatfeats(0),
                    featureVector.getFloatfeats(1),
                    featureVector.getFloatfeats(2),
                    featureVector.getFloatfeats(3) == 1.0);
        } else {
            return new RespirationStat(0, 0, 0, false);
        }
    }

    public RespirationStat(float breathDurationSeconds, float standardDevBPM, float energyDb, boolean hasRespiration) {
        this.breathDurationSeconds = breathDurationSeconds;
        this.standardDevBPM = standardDevBPM;
        this.energyDb = energyDb;
        this.hasRespiration = hasRespiration;
    }

    public float getBreathDurationSeconds() {
        return breathDurationSeconds;
    }

    public float getBreathsPerMinute() {
        return 60  / Math.max(breathDurationSeconds, 0);
    }

    public float getStandardDevBPM() {
        return standardDevBPM;
    }

    public float getEnergyDb() {
        return energyDb;
    }

    public boolean isHasRespiration() {
        return hasRespiration;
    }
}

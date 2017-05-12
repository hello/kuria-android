package is.hellos.demos.interactors.v1;

import android.support.annotation.NonNull;

import com.google.protobuf.InvalidProtocolBufferException;

import is.hellos.demos.interactors.BaseZMQInteractor;
import is.hellos.demos.models.protos.RadarMessages;
import is.hellos.demos.models.respiration.RespirationStat;

/**
 * Created by simonchen on 5/11/17.
 */

public class RespirationStatsInteractor extends BaseZMQInteractor<RespirationStat> {
    @Override
    protected String getTag() {
        return RespirationStatsInteractor.class.getSimpleName();
    }

    @Override
    protected void parseMessage(@NonNull byte[] message) throws InvalidProtocolBufferException {
        RadarMessages.FeatureVector featureVector = RadarMessages.FeatureVector.parseFrom(message);

        if (!featureVector.hasId()) {
            return;
        }

        if ("respiration".equals(featureVector.getId())) {
            this.observable.update(RespirationStat.convertFrom(featureVector));
        }
    }
}

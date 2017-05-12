package is.hellos.demos.interactors.v1;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Observer;

import is.hellos.demos.interactors.BaseZMQInteractor;
import is.hellos.demos.models.protos.RadarMessages;
import is.hellos.demos.models.respiration.RespirationProb;

/**
 * Created by simonchen on 5/10/17.
 */

public class RespirationProbsInteractor extends BaseZMQInteractor<RespirationProb> {

    public RespirationProbsInteractor() {
    }

    @Override
    protected String getTag() {
        return RespirationProbsInteractor.class.getSimpleName();
    }

    @Override
    protected void parseMessage(@NonNull byte[] message) throws InvalidProtocolBufferException {
        final RadarMessages.FeatureVector featureVector = RadarMessages.FeatureVector.parseFrom(message);
        final RespirationProb respirationProb = new RespirationProb(
                featureVector.getFloatfeats(0),
                featureVector.getFloatfeats(1),
                featureVector.getFloatfeats(2),
                featureVector.getFloatfeats(3));
        this.observable.update(respirationProb);
    }

    public void addObserver(@NonNull final Observer observer) {
        this.observable.addObserver(observer);
    }

    public void removeObserver(@Nullable final Observer observer) {
        this.observable.deleteObserver(observer);
    }
}

package is.hellos.demos.googleFit;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by simonchen on 5/16/17.
 */

class FitObserver implements Observer {

    private final FitObserverHandler handler;

    public FitObserver(final FitObserverHandler handler) {
        this.handler = handler;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof GoogleFitUtil.Wrapper)) {
            return;
        }
        final GoogleFitUtil.Wrapper wrapper = (GoogleFitUtil.Wrapper) arg;

        switch (wrapper.TAG) {
            case CONNECTION_FAILED:
                this.handler.onConnectionFailedResult((ConnectionResult) wrapper.VALUE);
                return;
            case CONNECTED:
                this.handler.onConnectionSuccess((Bundle) wrapper.VALUE);
                return;
            case SUBSCRIBED:
                this.handler.onSubscribeResult((Status) wrapper.VALUE);
                return;
            case UNSUBSCRIBED:
                this.handler.onUnSubscribeResult((Status) wrapper.VALUE);
                return;
            case SESSION_STARTED:
                this.handler.onSessionStartResult((Status) wrapper.VALUE);
                return;
            case SESSION_STOPPED:
                this.handler.onSessionStopResult((Status) wrapper.VALUE);
                return;
            case DISCONNECTED:
                this.handler.onDisconnectionResult((Status) wrapper.VALUE);
                return;
            default:
                throw new IllegalStateException("unsupported state " + wrapper.TAG);
        }
    }

    interface FitObserverHandler {
        void onConnectionFailedResult(ConnectionResult connectionResult);

        void onDisconnectionResult(Status status);

        void onConnectionSuccess(Bundle bundle);

        void onSubscribeResult(Status status);

        void onUnSubscribeResult(Status status);

        void onSessionStartResult(Status status);

        void onSessionStopResult(Status status);
    }

    }

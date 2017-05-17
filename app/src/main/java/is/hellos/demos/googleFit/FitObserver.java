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
        if (arg instanceof ConnectionResult) {
            this.handler.onConnectionResult((ConnectionResult) arg);
        } else if (arg instanceof Bundle) {
            this.handler.onConnectionSuccess((Bundle) arg);
        } else if (arg instanceof Status) {
            this.handler.onSubscribeResult((Status) arg);
        }
    }

    interface FitObserverHandler {
        void onConnectionResult(ConnectionResult connectionResult);

        void onConnectionSuccess(Bundle bundle);

        void onSubscribeResult(Status status);
    }
}

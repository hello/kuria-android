package is.hellos.demos.interactors;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Observable;
import java.util.Observer;

import is.hellos.demos.network.zmq.ZeroMQSubscriber;

/**
 * Created by simonchen on 5/10/17.
 */

public abstract class BaseZMQInteractor<T> implements ZeroMQSubscriber.Listener {

    protected BaseObservable<T> observable = new BaseObservable<>();

    protected abstract String getTag();

    protected abstract void parseMessage(@NonNull byte[] message) throws InvalidProtocolBufferException;

    public void addObserver(@NonNull final Observer observer) {
        this.observable.addObserver(observer);
    }

    public void removeObserver(@Nullable final Observer observer) {
        this.observable.deleteObserver(observer);
    }

    @Override
    public void onConnecting() {
        Log.d(getTag(), "on connecting");
    }

    @Override
    public void onConnected() {
        Log.d(getTag(), "on connected");
    }

    @Override
    public void onDisconnected() {
        Log.d(getTag(), "on disconnected");
    }

    @CallSuper
    @Override
    public void onMessageReceived(@NonNull byte[] message) {
        Log.d(getTag(), "on message received");
        try {
            parseMessage(message);
        } catch (InvalidProtocolBufferException e) {
            Log.e(getTag(), "action=onMessageReceived() improperly formatted protobuf", e);
        }
    }

    public static class BaseObservable<T> extends Observable {

        public void update(T value) {
            setChanged();
            notifyObservers(value);
            clearChanged();
        }
    }
}

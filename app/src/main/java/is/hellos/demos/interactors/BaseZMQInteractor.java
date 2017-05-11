package is.hellos.demos.interactors;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import is.hellos.demos.network.zmq.ZeroMQSubscriber;

/**
 * Created by simonchen on 5/10/17.
 */

public abstract class BaseZMQInteractor implements ZeroMQSubscriber.Listener {

    protected abstract String getTag();

    protected abstract void parseMessage(@NonNull byte[] message) throws InvalidProtocolBufferException;

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
}

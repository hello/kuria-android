package is.hellos.demos.network.zmq;

import android.util.Log;

public abstract class MessageReceivedListener implements ZeroMQSubscriber.Listener {

    @Override
    public void onConnecting() {
        Log.d(MessageReceivedListener.class.getSimpleName(), "onConnecting");
        // postToast(R.string.state_connecting);

    }

    @Override
    public void onConnected() {
        Log.d(MessageReceivedListener.class.getSimpleName(), "onConnected");
        //  postToast(R.string.state_connected);

    }

    @Override
    public void onDisconnected() {
        //   postToast(R.string.state_disconnected);

    }
}

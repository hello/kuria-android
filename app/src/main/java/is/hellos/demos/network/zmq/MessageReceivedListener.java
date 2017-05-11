package is.hellos.demos.network.zmq;

public abstract class MessageReceivedListener implements ZeroMQSubscriber.Listener {

    @Override
    public void onConnecting() {
        // postToast(R.string.state_connecting);

    }

    @Override
    public void onConnected() {
        //  postToast(R.string.state_connected);

    }

    @Override
    public void onDisconnected() {
        //   postToast(R.string.state_disconnected);

    }
}

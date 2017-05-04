package is.hellos.demos.network.zmq;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import org.zeromq.ZMQ;


public class ZeroMQSubscriber implements Runnable {
    public static final String PLOT_TOPIC = "PLOT";
    public static final String STATS_TOPIC = "STATS";
  //  private static final String IP_ADDRESS = "tcp://192.168.128.119:5564";
    private static final String IP_ADDRESS = "tcp://192.168.129.43:5564";

    private static final Listener EMPTY_LISTENER = new Listener() {
        @Override
        public void onConnecting() {

        }

        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onMessageReceived(@NonNull byte[] message) {

        }
    };

    @NonNull
    private Listener listener = EMPTY_LISTENER;
    private final String topic;

    public ZeroMQSubscriber(@NonNull final String topic) {
        this.topic = topic;
    }

    @Override
    public void run() {
        this.listener.onConnecting();
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.SUB);
        socket.connect(IP_ADDRESS);
        socket.subscribe((this.topic).getBytes());
        this.listener.onConnected();
        while (!Thread.currentThread().isInterrupted()) {
            this.listener.onMessageReceived(socket.recv(0));
        }
        socket.close();
        context.term();
        this.listener.onDisconnected();
    }


    public void setListener(@Nullable final Listener listener) {
        if (listener == null) {
            this.listener = EMPTY_LISTENER;
            return;
        }
        this.listener = listener;
    }


    public interface Listener {
        void onConnecting();

        void onConnected();

        void onDisconnected();

        void onMessageReceived(@NonNull final byte[] message);
    }

}
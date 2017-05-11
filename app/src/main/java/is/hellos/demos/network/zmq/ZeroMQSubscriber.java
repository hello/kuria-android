package is.hellos.demos.network.zmq;

import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;


public class ZeroMQSubscriber implements Runnable {
    public static final String BASEBAND_TOPIC = "v1/baseband";
    public static final String PLOT_TOPIC = "PLOT";
    public static final String STATS_TOPIC = "STATS";

    /**
     * Feature Vector
     * id = ""
     *  0 exhaled
        1 inhaling
        2 inhaled
        3 exhaling
        values are between 0 and 1
        pretty sensitive and will hit 0 if target moves
     */
    public static final String RESPIRATION_PROBS_TOPIC = "v1/breathprobs";
    /**
     * Feature Vector
     * id = "respiration"
     * you can get zero probabilities and there will still be respiration , or activity
       0 is mean period of "breaths" in seconds
       1 is standard dev of period of breaths
       2 is energy of signal in decibels -- less than 20 probably means nothing is there (20 - 40) scaling could be completely different depending on data source
       3 is boolean is has respiration
     */
    public static final String RESPIRATION_STATS_TOPIC = "v1/respiration_stats";

    /**
     * {@link is.hellos.demos.models.protos.RespirationHealth.RespirationStatus}
     * {@link is.hellos.demos.models.protos.RespirationHealth.ResiprationHealthState}
     * 0 - no one
     * 1 - someone breathing
     * 2 - someone not breathing
     */
    private static final String BABY_STATE_TOPIC = "v1/baby_state";
    private static final String BABY_STATE_IP_ADDRESS = "tcp://192.168.128.40:5565";
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
    private final String ipAddress;
    private ZMQ.Context context;
    private ZMQ.Socket socket;

    public static ZeroMQSubscriber getBabyStateSubscriber() {
        return new ZeroMQSubscriber(BABY_STATE_TOPIC, BABY_STATE_IP_ADDRESS);
    }

    public ZeroMQSubscriber(@NonNull final String topic) {
        this(topic, IP_ADDRESS);
    }

    public ZeroMQSubscriber(@NonNull final String topic,
                            @NonNull final String ipAddress) {
        this.topic = topic;
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        this.listener.onConnecting();
        this.context = ZMQ.context(1);
        this.socket = context.socket(ZMQ.SUB);
        socket.connect(ipAddress);
        socket.subscribe((this.topic).getBytes());
        this.listener.onConnected();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                this.listener.onMessageReceived(socket.recv(0));
            }
        } catch(ZMQException e) {
            Log.e(ZeroMQSubscriber.class.getSimpleName(),
                    String.format("action=run() Exception while subscribed to topic %s at ip address %s",topic, ipAddress),
                    e);
        }finally {
            stop();
        }
    }

    public void stop() {
        try {
            socket.close();
            context.term();
            this.listener.onDisconnected();
        } catch (ZMQException.IOException | NetworkOnMainThreadException e) {
            Log.e(ZeroMQSubscriber.class.getSimpleName(),
                    String.format("action=stop() Exception while subscribed to topic %s at ip address %s",topic, ipAddress),
                    e);
        }
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
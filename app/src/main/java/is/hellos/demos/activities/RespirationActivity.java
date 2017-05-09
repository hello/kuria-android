package is.hellos.demos.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import butterknife.BindView;
import is.hellos.demos.R;
import is.hellos.demos.graphs.respiration.RespirationView;
import is.hellos.demos.models.protos.RadarMessages;
import is.hellos.demos.models.respiration.RespirationStat;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;
import is.hellos.demos.utils.HapticUtil;

/**
 * Created by simonchen on 5/8/17.
 */

public class RespirationActivity extends BaseActivity
implements ZeroMQSubscriber.Listener{

    final int FPS = 20;

    private long lastUpdatedMillis = System.currentTimeMillis();

    private HapticUtil hapticUtil;
    private RespirationBroadcastReceiver respirationBroadcastReceiver;
    private Handler handler = new Handler();
    @BindView(R.id.activity_respiration_state_text_view)
    TextView stateTextView;
    @BindView(R.id.activity_respiration_respiration_view)
    RespirationView respirationView;
    ZeroMQSubscriber zeroMQSubscriber;

    private final static String LOCAL_DATA_SOURCE = "tcp://192.168.128.57:5564";

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_respiration;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.zeroMQSubscriber = new ZeroMQSubscriber(ZeroMQSubscriber.RESPIRATION_STATS_TOPIC
                                                    );
        this.zeroMQSubscriber.setListener(this);
        this.hapticUtil = new HapticUtil(this);
        this.respirationBroadcastReceiver = new RespirationBroadcastReceiver(hapticUtil);
        final Thread subscriberThread = new Thread(zeroMQSubscriber);
        subscriberThread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                this.respirationBroadcastReceiver,
                RespirationBroadcastReceiver.getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                this.respirationBroadcastReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.hapticUtil != null) {
            this.hapticUtil.cancel();
            this.hapticUtil = null;
        }

        if (this.zeroMQSubscriber != null) {
            this.zeroMQSubscriber.stop();
            this.zeroMQSubscriber = null;
        }

        if (this.respirationBroadcastReceiver != null) {
            this.respirationBroadcastReceiver = null;
        }
    }

    @Override
    public void onConnecting() {
        updateState(getString(R.string.state_connecting));
    }

    @Override
    public void onConnected() {
        updateState(getString(R.string.state_connected));

    }

    @Override
    public void onDisconnected() {
        updateState(getString(R.string.state_disconnected));
        if (this.hapticUtil != null) {
            this.hapticUtil.cancel();
        }
    }


    @Override
    public void onMessageReceived(@NonNull final byte[] message) {
        try {
            RadarMessages.FeatureVector featureVector = RadarMessages.FeatureVector.parseFrom(message);

            if (!featureVector.hasId()) {
                return;
            }

            final String output = featureVector.toString();
            RespirationActivity.this.updateState(output);

            if ("respiration".equals(featureVector.getId())) {
                final long currentTimeMillis = System.currentTimeMillis();
                if ((currentTimeMillis - this.lastUpdatedMillis ) / (1000 * FPS) > FPS) {
                    return; //drop message
                }
                this.lastUpdatedMillis = currentTimeMillis;
                final RespirationStat respirationStat = RespirationStat.convertFrom(featureVector);
                RespirationActivity.this.updateView(respirationStat);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateState(@NonNull final String state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                RespirationActivity.this.stateTextView.setText(state);
            }
        });
    }

    void updateView(final RespirationStat respirationStat) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                RespirationActivity.this.respirationView.update(respirationStat);
            }
        });
    }

    public static class RespirationBroadcastReceiver extends BroadcastReceiver {
        private static final String RESPIRATION_ACTION = RespirationBroadcastReceiver.class.getSimpleName() + "_RESPIRATION_ACTION";
        private static final String EXTRA_DURATION = RespirationBroadcastReceiver.class.getSimpleName() + "_DURATION_EXTRA";

        private final HapticUtil hapticUtil;

        public static IntentFilter getIntentFilter() {
            return new IntentFilter(RESPIRATION_ACTION);
        }

        public static Intent getIntent(@NonNull final RespirationStat respirationStat) {
            return new Intent(RESPIRATION_ACTION).putExtra(EXTRA_DURATION, respirationStat.getBreathDurationSeconds() * 100);
        }

        RespirationBroadcastReceiver(HapticUtil hapticUtil) {
            this.hapticUtil = hapticUtil;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (RESPIRATION_ACTION.equals(intent.getAction())) {
                final long duration = (long) intent.getFloatExtra(EXTRA_DURATION, 0);
                hapticUtil.vibrate(duration);
            }
        }
    }
}

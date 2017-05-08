package is.hellos.demos.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private HapticUtil hapticUtil;
    private Handler handler = new Handler();
    @BindView(R.id.activity_respiration_state_text_view)
    TextView stateTextView;
    @BindView(R.id.activity_respiration_respiration_view)
    RespirationView respirationView;
    ZeroMQSubscriber zeroMQSubscriber;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_respiration;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.zeroMQSubscriber = new ZeroMQSubscriber(ZeroMQSubscriber.RESPIRATION_STATS_TOPIC,
                                                    "tcp://192.168.128.57:5564");
        this.zeroMQSubscriber.setListener(this);
        this.hapticUtil = new HapticUtil(this);
        final Thread subscriberThread = new Thread(zeroMQSubscriber);
        subscriberThread.start();

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
}

package is.hellos.demos.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import is.hellos.demos.R;
import is.hellos.demos.broadcastreceivers.HapticFeedbackBroadcastReceiver;
import is.hellos.demos.graphs.respiration.RespirationView;
import is.hellos.demos.interactors.v1.RespirationProbsInteractor;
import is.hellos.demos.models.protos.RadarMessages;
import is.hellos.demos.models.respiration.RespirationProb;
import is.hellos.demos.models.respiration.RespirationStat;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;
import is.hellos.demos.utils.HapticUtil;

/**
 * Created by simonchen on 5/8/17.
 */

public class RespirationActivity extends BaseActivity
implements ZeroMQSubscriber.Listener, Observer{

    final int FPS = 20;

    private long lastUpdatedMillis = System.currentTimeMillis();

    private HapticUtil hapticUtil;
    private HapticFeedbackBroadcastReceiver hapticFeedbackBroadcastReceiver;
    private Handler handler = new Handler();
    @BindView(R.id.activity_respiration_state_text_view)
    TextView stateTextView;
    @BindView(R.id.activity_respiration_stats_text_view)
    TextView respirationStatsTextView;
    @BindView(R.id.activity_respiration_respiration_view)
    RespirationView respirationView;
    @BindView(R.id.activity_respiration_prob_text_view)
    TextView respirationProbsTextView;
    ZeroMQSubscriber respirationStatsSubscriber;
    ZeroMQSubscriber respirationProbsSubscriber;

    private HandlerThread handlerThread;
    private RespirationProbsInteractor probsInteractor;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_respiration;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stateTextView.setVisibility(View.GONE);
        respirationStatsTextView.setVisibility(View.GONE);
        respirationProbsTextView.setVisibility(View.GONE);

        this.respirationStatsSubscriber = new ZeroMQSubscriber(ZeroMQSubscriber.RESPIRATION_STATS_TOPIC);
        this.respirationStatsSubscriber.setListener(this);
        this.hapticUtil = new HapticUtil(this);
        this.hapticFeedbackBroadcastReceiver = new HapticFeedbackBroadcastReceiver(hapticUtil);
        //todo see if using same handler for these 2 runnables will not block each other
        final Thread subscriberThread = new Thread(respirationStatsSubscriber);
        subscriberThread.start();

        this.probsInteractor = new RespirationProbsInteractor();
        this.probsInteractor.addObserver(this);
        this.respirationProbsSubscriber = new ZeroMQSubscriber(ZeroMQSubscriber.RESPIRATION_PROBS_TOPIC);
        this.respirationProbsSubscriber.setListener(probsInteractor);
        this.handlerThread = new HandlerThread("Respiration Probs", HandlerThread.NORM_PRIORITY);
        this.handlerThread.start();
        final Handler subscriberHandler = new Handler(handlerThread.getLooper());
        subscriberHandler.post(this.respirationProbsSubscriber);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                this.hapticFeedbackBroadcastReceiver,
                HapticFeedbackBroadcastReceiver.getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                this.hapticFeedbackBroadcastReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.hapticUtil != null) {
            this.hapticUtil.cancel();
            this.hapticUtil = null;
        }

        if (this.probsInteractor != null) {
            this.probsInteractor.removeObserver(this);
            this.probsInteractor = null;
        }

        if (this.respirationStatsSubscriber != null) {
            this.respirationStatsSubscriber.stop();
            this.respirationStatsSubscriber = null;
        }

        if (this.respirationProbsSubscriber != null) {
            this.respirationProbsSubscriber.stop();
            this.respirationProbsSubscriber = null;
        }

        if (this.handlerThread != null) {
            this.handlerThread.quitSafely();
            this.handlerThread = null;
        }

        if (this.hapticFeedbackBroadcastReceiver != null) {
            this.hapticFeedbackBroadcastReceiver = null;
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

            if ("respiration".equals(featureVector.getId())) {
                final long currentTimeMillis = System.currentTimeMillis();
                if (shouldDropMessage()) {
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

    //region Observer
    @Override
    public void update(Observable o, Object arg) {
        if (shouldDropMessage()) {
            return; //drop message
        }

        if (arg instanceof RespirationProb) {
            final RespirationProb respirationProb = (RespirationProb) arg;
            RespirationActivity.this.updateView(respirationProb);
        }
    }
    //endregion

    boolean shouldDropMessage() {
        return ( System.currentTimeMillis() - this.lastUpdatedMillis ) / (1000 * FPS) > FPS;
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
                RespirationActivity.this.respirationStatsTextView.setText(respirationStat.toString());
                RespirationActivity.this.respirationView.update(respirationStat);
            }
        });
    }

    void updateView(final RespirationProb respirationProb) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                RespirationActivity.this.respirationProbsTextView.setText(respirationProb.toString());
                RespirationActivity.this.respirationView.update(respirationProb);
            }
        });
    }
}

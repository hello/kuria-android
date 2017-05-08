package is.hellos.demos.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import is.hellos.demos.R;
import is.hellos.demos.graphs.waves.WaveGraphView;
import is.hellos.demos.models.protos.RadarMessages;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;

public class WaveActivity extends BaseActivity
        implements ZeroMQSubscriber.Listener {

    @BindView(R.id.activity_wave_state)
    TextView stateTextView;
    @BindView(R.id.activity_wave_action)
    Button actionButton;
    @BindView(R.id.activity_wave_graph)
    WaveGraphView waveGraphView;
    private final Handler handler = new Handler();
    private boolean pauseOutput;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_wave;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ZeroMQSubscriber zeroMQSubscriber = new ZeroMQSubscriber(ZeroMQSubscriber.PLOT_TOPIC);
        zeroMQSubscriber.setListener(this);
        new Thread(zeroMQSubscriber).start();
        updateUI();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseOutput = !pauseOutput;
                updateUI();
            }
        });
    }

    @Override
    public void onConnecting() {
        // stateTextView.setText(R.string.state_connecting);

    }

    @Override
    public void onConnected() {
        // stateTextView.setText(R.string.state_connected);

    }

    @Override
    public void onDisconnected() {
        // stateTextView.setText(R.string.state_disconnected);
    }


    @Override
    public void onMessageReceived(@NonNull final byte[] message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (pauseOutput) {
                        return;
                    }

                    RadarMessages.FeatureVector featureVector = RadarMessages.FeatureVector.parseFrom(message);

                    if (!featureVector.hasId()) {
                        return;
                    }

                    if (!featureVector.getId().equals("maxvarresp")) {
                        return;
                    }

                    String output = featureVector.toString();
                    stateTextView.setText(output);
                    waveGraphView.update(featureVector.getFloatfeats(0), featureVector.getFloatfeats(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUI() {
        actionButton.setText(pauseOutput ? R.string.action_resume : R.string.action_pause);
    }
}

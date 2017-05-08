package is.hellos.demos.activities;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import butterknife.BindView;
import is.hellos.demos.R;
import is.hellos.demos.graphs.time.TimeGraphView;
import is.hellos.demos.models.protos.RadarMessages;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;

public class TimeActivity extends BaseActivity
        implements ZeroMQSubscriber.Listener {

    @BindView(R.id.activity_time_state)
    TextView stateTextView;
    @BindView(R.id.activity_time_action)
    Button actionButton;
    @BindView(R.id.activity_time_graph)
    TimeGraphView timeGraphView;
    private final Handler handler = new Handler();
    private boolean pauseOutput;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_time;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ZeroMQSubscriber zeroMQSubscriber = new ZeroMQSubscriber(ZeroMQSubscriber.BASEBAND_TOPIC);
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
    protected void onDestroy() {
        super.onDestroy();

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
                    String output = featureVector.toString();
                    stateTextView.setText(output);
                    //   Log.e(RadarActivity.class.getSimpleName(), output);
                    timeGraphView.addValue(featureVector.getFloatfeats(0), featureVector.getFloatfeats(1));
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUI() {
        actionButton.setText(pauseOutput ? R.string.action_resume : R.string.action_pause);
    }
}

package is.hellos.demos.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import butterknife.BindView;
import is.hellos.demos.R;
import is.hellos.demos.graphs.timelines.TimelineGraphView;
import is.hellos.demos.models.timeline.Item;
import is.hellos.demos.models.timeline.TimelineRunnable;
import is.hellos.demos.models.protos.RadarMessages;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;

public class TimelineActivity extends BaseActivity
        implements ZeroMQSubscriber.Listener,
        TimelineRunnable.Listener {

    @BindView(R.id.activity_timeline_state)
    TextView stateTextView;
    @BindView(R.id.activity_timeline_action)
    Button actionButton;
    @BindView(R.id.activity_timeline_graph)
    TimelineGraphView timelineGraphView;
    private final Handler handler = new Handler();
    private boolean pauseOutput;
    TimelineRunnable timelineRunnable = new TimelineRunnable();

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_timeline;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ZeroMQSubscriber zeroMQSubscriber = new ZeroMQSubscriber(ZeroMQSubscriber.STATS_TOPIC);
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
        this.timelineRunnable.setListener(this);
        new Thread(timelineRunnable).start();
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
    public void onUpdated() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                timelineGraphView.setRoot(timelineRunnable.getGraphTimeTracker().getRoot());
            }
        });
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
                    final RadarMessages.FeatureVector featureVector = RadarMessages.FeatureVector.parseFrom(message);
                    String output = featureVector.toString();
                    stateTextView.setText(output);
                  /*  timelineRunnable.addGraphTime(new Item() {
                        @Override
                        public float getHeight() {
                            return featureVector.getFloatfeats(0);
                        }
                    });*/
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



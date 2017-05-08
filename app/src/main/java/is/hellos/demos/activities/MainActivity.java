package is.hellos.demos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import is.hellos.demos.R;

public class MainActivity extends BaseActivity {

    @BindView(R.id.activity_textview_radar_graph_one)
    TextView graphOneTextView;
    @BindView(R.id.activity_textview_timeline_graph_one)
    TextView graphTwoTextView;
    @BindView(R.id.activity_textview_wave_graph_one)
    TextView waveGraph;
    @BindView(R.id.activity_textview_respiration_graph)
    TextView respirationGraph;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graphOneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RadarActivity.class));
            }
        });
        graphTwoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TimelineActivity.class));
            }
        });
        waveGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WaveActivity.class));
            }
        });
        respirationGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RespirationActivity.class));
            }
        });

    }

}

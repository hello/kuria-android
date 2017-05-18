package is.hellos.demos.googleFit;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import is.hellos.demos.R;
import is.hellos.demos.activities.BaseActivity;

/**
 * Created by simonchen on 5/17/17.
 */

public class GoogleFitActivity extends BaseActivity implements FitObserver.FitObserverHandler {
    private static final int REQUEST_CODE_PENDING_RESOLUTION = 0xdead;
    private static final int REQUEST_CODE_BODY_SENSOR_PERMISSION = 0xbeef;
    private GoogleFitUtil googleFitUtil;
    @Nullable
    private GoogleFitFragment googleFitFragment;

    @NonNull
    private final GoogleFitBroadcastReceiver googleFitBroadcastReceiver = new GoogleFitBroadcastReceiver();
    private boolean sessionStarted;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_google_fit;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentByTag(GoogleFitFragment.TAG) == null) {
            googleFitFragment = GoogleFitFragment.create();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_google_fit_container, googleFitFragment, GoogleFitFragment.TAG)
                    .commit();
        }

        if (this.googleFitUtil == null) {
            this.googleFitUtil = new GoogleFitUtil(this.getApplicationContext());
        }
        this.googleFitUtil.start(new FitObserver(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_PENDING_RESOLUTION == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                this.onConnectionSuccess(null);
            } else {
                Log.i(GoogleFitActivity.class.getSimpleName(), "check if build configs and app package name match accepted OAuth clients");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onConnectionSuccess(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                googleFitBroadcastReceiver,
                googleFitBroadcastReceiver.getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(googleFitBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.googleFitUtil != null) {
            this.googleFitUtil.removeObservers();
            if (isFinishing()) {
                this.googleFitUtil.end();
                this.googleFitUtil = null;
            }
        }

        if (this.googleFitFragment != null && isFinishing()) {
            this.googleFitFragment = null;
        }
    }

    @Override
    public void onConnectionFailedResult(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_PENDING_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnectionResult(Status status) {

    }

    @Override
    public void onConnectionSuccess(Bundle ignored) {
        Toast.makeText(this, "connected to google api client with fit API", Toast.LENGTH_SHORT).show();
        if (!hasPermission()) {
            requestPermission();
            return;
        }

        if (this.googleFitUtil != null) {
            this.googleFitUtil.subscribe();
        }
    }

    boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BODY_SENSORS}, REQUEST_CODE_BODY_SENSOR_PERMISSION);
    }

    @Override
    public void onSubscribeResult(Status status) {
        if (googleFitFragment != null) {
            googleFitFragment.setSessionEnabled(status.isSuccess());
        }
    }

    @Override
    public void onUnSubscribeResult(Status status) {
        if (googleFitFragment != null) {
            googleFitFragment.setSessionEnabled(!status.isSuccess());
        }
    }

    @Override
    public void onSessionStartResult(Status status) {
        this.sessionStarted = status.isSuccess();
        if (googleFitFragment != null) {
            googleFitFragment.setSessionButtonText(sessionStarted ? "Stop Session" : "Start Session");
        }
        if (googleFitUtil != null) {
            googleFitUtil.insertData();
        }
    }

    @Override
    public void onSessionStopResult(Status status) {
        this.sessionStarted = !status.isSuccess();
        if (googleFitFragment != null) {
            googleFitFragment.setSessionButtonText(sessionStarted ? "Stop Session" : "Start Session");
        }
    }

    public void toggleSession() {
        if (this.googleFitUtil == null) {
            return;
        }
        if (sessionStarted) {
            this.googleFitUtil.stopSession();
        } else  {
            this.googleFitUtil.startSession();
        }
    }

    public interface View {
        void setSessionEnabled(boolean isEnabled);
        void setSessionButtonText(String text);
    }

    public class GoogleFitBroadcastReceiver extends BroadcastReceiver {

        public static final String ACTION_SESSION_CLICK = "action_session_click";

        public IntentFilter getIntentFilter() {
            return new IntentFilter(ACTION_SESSION_CLICK);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SESSION_CLICK.equals(intent.getAction())) {
                GoogleFitActivity.this.toggleSession();
            }
        }
    }
}

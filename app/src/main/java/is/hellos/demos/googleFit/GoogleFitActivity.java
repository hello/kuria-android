package is.hellos.demos.googleFit;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private GoogleFitUtil googleFitUtil;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_google_fit;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentByTag(GoogleFitFragment.TAG) == null) {
            final GoogleFitFragment googleFitFragment = GoogleFitFragment.create();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_google_fit_container, googleFitFragment, GoogleFitFragment.TAG)
                    .commit();
        }
        this.googleFitUtil = new GoogleFitUtil(this);
        this.googleFitUtil.start(new FitObserver(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_PENDING_RESOLUTION == requestCode) {
            this.googleFitUtil.subscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.googleFitUtil != null) {
            this.googleFitUtil.end();
            this.googleFitUtil = null;
        }
    }

    @Override
    public void onConnectionResult(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_PENDING_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuccess(Bundle bundle) {
        Toast.makeText(this, "connected to google api client with fit API", Toast.LENGTH_SHORT).show();
        if (this.googleFitUtil != null) {
            this.googleFitUtil.subscribe();
        }
    }

    @Override
    public void onSubscribeResult(Status status) {
        if (status.isSuccess()) {
            if (this.googleFitUtil != null) {
                this.googleFitUtil.startSession();
            }
        }
    }
}

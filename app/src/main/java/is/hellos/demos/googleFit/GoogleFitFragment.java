package is.hellos.demos.googleFit;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import is.hellos.demos.R;

/**
 * Created by simonchen on 5/17/17.
 */

public class GoogleFitFragment extends Fragment implements GoogleFitActivity.View, View.OnClickListener {

    public static final String TAG = GoogleFitFragment.class.getSimpleName() + "_TAG";
    private Button sessionButton;

    public static GoogleFitFragment create() {
        return new GoogleFitFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_google_fit, container, false);
        sessionButton = (Button) view.findViewById(R.id.fragment_google_fit_session_button);
        setSessionEnabled(false);
        sessionButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void setSessionEnabled(boolean isEnabled) {
        if (sessionButton != null) {
            sessionButton.setEnabled(isEnabled);
        }
    }

    @Override
    public void setSessionButtonText(String text) {
        if (sessionButton != null) {
            sessionButton.setText(text);
        }
    }

    @Override
    public void onClick(View v) {
        final Intent sessionButtonClickedIntent = new Intent(GoogleFitActivity.GoogleFitBroadcastReceiver.ACTION_SESSION_CLICK);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(sessionButtonClickedIntent);
    }
}

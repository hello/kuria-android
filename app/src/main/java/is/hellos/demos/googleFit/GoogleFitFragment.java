package is.hellos.demos.googleFit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import is.hellos.demos.R;

/**
 * Created by simonchen on 5/17/17.
 */

public class GoogleFitFragment extends Fragment {

    public static final String TAG = GoogleFitFragment.class.getSimpleName() + "_TAG";

    public static GoogleFitFragment create() {
        return new GoogleFitFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_google_fit, container, false);
        return view;
    }
}

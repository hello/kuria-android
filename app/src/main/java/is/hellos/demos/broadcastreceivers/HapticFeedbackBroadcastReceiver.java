package is.hellos.demos.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import is.hellos.demos.utils.HapticUtil;

/**
 * Created by simonchen on 5/9/17.
 */
public class HapticFeedbackBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION_VIBRATE = HapticFeedbackBroadcastReceiver.class.getSimpleName() + "_ACTION_VIBRATE";
    private static final String EXTRA_DURATION = HapticFeedbackBroadcastReceiver.class.getSimpleName() + "_DURATION_EXTRA";

    private final HapticUtil hapticUtil;

    public static IntentFilter getIntentFilter() {
        return new IntentFilter(ACTION_VIBRATE);
    }

    public static Intent getIntent(final long duration) {
        return new Intent(ACTION_VIBRATE).putExtra(EXTRA_DURATION, duration);
    }

    public HapticFeedbackBroadcastReceiver(HapticUtil hapticUtil) {
        this.hapticUtil = hapticUtil;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_VIBRATE.equals(intent.getAction())) {
            final long duration = (long) intent.getFloatExtra(EXTRA_DURATION, 0);
            hapticUtil.vibrate(duration);
        }
    }
}

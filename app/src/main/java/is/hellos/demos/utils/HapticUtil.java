package is.hellos.demos.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import static android.Manifest.permission.VIBRATE;

/**
 * Created by simonchen on 5/4/17.
 */

public class HapticUtil {

    private final Vibrator vibrator;
    private final AudioAttributes audioAttributes;

    public HapticUtil(@NonNull final Context context) {
       this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
    }

    @RequiresPermission(VIBRATE)
    public void vibrate(final long durationMillis) {
        if (this.vibrator.hasVibrator()) {
            this.vibrator.vibrate(durationMillis, audioAttributes);
        } else {
            Log.w(HapticUtil.class.getSimpleName(), "no vibrate hardware for device");
        }
    }

    /**
     * Call when vibration should end like onDestroyView or onPause.
     */
    @RequiresPermission(VIBRATE)
    public void cancel() {
        this.vibrator.cancel();
    }
}

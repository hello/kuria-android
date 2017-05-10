package is.hellos.demos.broadcastreceivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import is.hellos.demos.R;
import is.hellos.demos.models.notification.Notification;

/**
 * Created by simonchen on 5/9/17.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = NotificationBroadcastReceiver.class.getSimpleName();
    private static final String ACTION_PUSH = NotificationBroadcastReceiver.class.getSimpleName() + "_ACTION_PUSH";
    private static final String ACTION_CANCEL = NotificationBroadcastReceiver.class.getSimpleName() + "_ACTION_CANCEL";
    private static final String EXTRA_NOTIFICATION = NotificationBroadcastReceiver.class.getSimpleName() + "_EXTRA_NOTIFICATION";
    private static final String EXTRA_NOTIFICATION_TAG = NotificationBroadcastReceiver.class.getSimpleName() + "_EXTRA_NOTIFICATION_TAG";

    public static IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PUSH);
        intentFilter.addAction(ACTION_CANCEL);
        return intentFilter;
    }

    public static Intent getPushIntent(@NonNull final Notification notification) {
        return new Intent(ACTION_PUSH)
                .putExtra(EXTRA_NOTIFICATION, notification);
    }

    public static Intent getCancelIntent(@NonNull final int notificationTag) {
        return new Intent(ACTION_CANCEL)
                .putExtra(EXTRA_NOTIFICATION_TAG, notificationTag);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ACTION_CANCEL.equals(intent.getAction()) && intent.hasExtra(EXTRA_NOTIFICATION_TAG)) {
            final int tag = intent.getIntExtra(EXTRA_NOTIFICATION_TAG, -1);
            if (tag != -1) {
                NotificationManagerCompat.from(context).cancel(tag);
            }
            return;
        }

        if (!ACTION_PUSH.equals(intent.getAction()) || !intent.hasExtra(EXTRA_NOTIFICATION)) {
            Log.w(TAG, "empty or wrong action notification intent");
            return;
        }

        final Notification notification = (Notification) intent.getSerializableExtra(EXTRA_NOTIFICATION);
        final Intent activityIntent = new Intent(context, notification.getTargetClass());
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(notification.getTitle());
        builder.setContentText(notification.getMsg());
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
                activityIntent,
                PendingIntent.FLAG_ONE_SHOT));
        builder.setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(notification.getTag(),
                builder.build());
    }
}

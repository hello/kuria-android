package is.hellos.demos.interactors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import is.hellos.demos.R;
import is.hellos.demos.activities.RespirationActivity;
import is.hellos.demos.broadcastreceivers.NotificationBroadcastReceiver;
import is.hellos.demos.models.notification.Notification;
import is.hellos.demos.models.protos.RespirationHealth;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;

import static is.hellos.demos.models.protos.RespirationHealth.ResiprationHealthState.PERSON_IS_PRESENT_NOT_BREATHING;

/**
 * Created by simonchen on 5/9/17.
 */

public class BabyStateInteractor implements ZeroMQSubscriber.Listener {
    private static final String TAG = BabyStateInteractor.class.getSimpleName() + "_TAG";
    private static final int BABY_STATE_TAG = 0;
    private final Context context;

    public BabyStateInteractor(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public void onConnecting() {
        Log.d(TAG, "connecting to baby state subscriber");
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "connected to baby state subscriber");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected from baby state subscriber");
    }

    @Override
    public void onMessageReceived(@NonNull byte[] message) {
        Log.d(TAG, "message received from baby state subscriber");
        try {
            final RespirationHealth.RespirationStatus respirationHealth = RespirationHealth.RespirationStatus.parseFrom(message);
            if (!respirationHealth.hasHealthState()
                    || !PERSON_IS_PRESENT_NOT_BREATHING.equals(respirationHealth.getHealthState())
                    ) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(NotificationBroadcastReceiver.getCancelIntent(BABY_STATE_TAG));
                return;
            }

            final Notification notification = Notification.getImportantNotification(
                    context.getString(R.string.notification_not_breathing_title),
                    context.getString(R.string.notification_not_breathing_msg),
                    BABY_STATE_TAG,
                    RespirationActivity.class
                    );
            LocalBroadcastManager.getInstance(context).sendBroadcast(NotificationBroadcastReceiver.getPushIntent(notification));

        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG, "action=onMessageReceived() babyStateSubscriber could not parse protobuf", e);
        }
    }
}

package is.hellos.demos.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;

import java.text.DecimalFormat;
import java.util.Locale;

import butterknife.BindView;
import is.hellos.demos.R;
import is.hellos.demos.models.protos.RadarMessages;
import is.hellos.demos.models.protos.RespirationHealth;
import is.hellos.demos.models.respiration.RespirationStat;
import is.hellos.demos.network.zmq.MessageReceivedListener;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;

import static is.hellos.demos.models.protos.RespirationHealth.ResiprationHealthState.NOBODY_PRESENT;
import static is.hellos.demos.models.protos.RespirationHealth.ResiprationHealthState.PERSON_IS_PRESENT_NOT_BREATHING;
import static is.hellos.demos.network.zmq.ZeroMQSubscriber.RESPIRATION_STATS_TOPIC;

public class SettingsActivity extends BaseActivity {

    private static final int REQUEST_DEATH = 1000;
    private static final int NOTIFICATION_ID = 50;
    private static final String EXTRA_OK = SettingsActivity.class.getSimpleName() + ".EXTRA_OK";
    private Handler handler = new Handler();
    private NotificationManager notificationManager;
    private int currentRpm = -1;

    @BindView(R.id.activity_settings_switch_death)
    Switch deathSwitch;

    private CompoundButton.OnCheckedChangeListener deathSwitchCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getMainApplication().setNotifyDeath(isChecked);
            updateState();
        }
    };


    public ZeroMQSubscriber.Listener babyStateListener = new MessageReceivedListener() {

        @Override
        public void onMessageReceived(@NonNull final byte[] message) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        final RespirationHealth.RespirationStatus respirationHealth = RespirationHealth.RespirationStatus.parseFrom(message);
                        if (getMainApplication().shouldNotifyDeath()) {
                            updateNotification(respirationHealth);
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    };

    public ZeroMQSubscriber.Listener respirationListener = new MessageReceivedListener() {

        @Override
        public void onMessageReceived(@NonNull final byte[] message) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        final RadarMessages.FeatureVector featureVector = RadarMessages.FeatureVector.parseFrom(message);
                        RespirationStat respirationStat = RespirationStat.convertFrom(featureVector);
                        currentRpm = ((int) respirationStat.getBreathsPerMinute());
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    };

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        updateState();

        ZeroMQSubscriber babyStateSubscriber = ZeroMQSubscriber.getBabyStateSubscriber();
        babyStateSubscriber.setListener(babyStateListener);
        new Thread(babyStateSubscriber).start();

        ZeroMQSubscriber respirationSubscriber = new ZeroMQSubscriber(RESPIRATION_STATS_TOPIC);
        respirationSubscriber.setListener(respirationListener);
        new Thread(respirationSubscriber).start();
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null || intent.getExtras() == null) {
            return;
        }
        Log.e(getClass().getSimpleName(), "received intent");
        if (intent.getExtras().containsKey(EXTRA_OK)) {
            final boolean isOk = intent.getBooleanExtra(EXTRA_OK, true);
            if (isOk) {
                this.deathSwitch.setChecked(false);
            } else {

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    //endregion

    private void postToast(@StringRes final int stringRes) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingsActivity.this, stringRes, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateState() {
        final boolean wantsDeathNotification = getMainApplication().shouldNotifyDeath();
        this.deathSwitch.setOnCheckedChangeListener(null);
        this.deathSwitch.setChecked(wantsDeathNotification);
        this.deathSwitch.setOnCheckedChangeListener(deathSwitchCheckChangedListener);
        if (wantsDeathNotification) {
            startNotification();
        } else {
            stopNotification();
        }

    }


    private void startNotification() {
        updateNotification(null);
    }

    private void updateNotification(@Nullable final RespirationHealth.RespirationStatus status) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_child_care_white_24dp);
        final String titleText;
        final String messageText;
        if (status == null || !status.hasHealthState()) {
            titleText = "Detecting your baby...";
            messageText = "Make sure your baby is in view of Sati.";
        } else {
            if (PERSON_IS_PRESENT_NOT_BREATHING.equals(status.getHealthState())) {
                titleText = "Your baby is dead.";
                messageText = "Check if your baby died. Click send help to contact 911.";
                builder.setPriority(Notification.PRIORITY_HIGH);
                builder.setVibrate(new long[]{1, 5, 1});
                builder.addAction(getHelpAction());
            } else if (NOBODY_PRESENT.equals(status.getHealthState())) {
                titleText = "Your baby is missing";
                messageText = "Make sure it didn't fall out of its crib!";
            } else {
                titleText = "Your baby is breathing fine.";
                if (currentRpm == -1) {
                    messageText = "That's some good breathing";
                } else {
                    messageText = "Your baby is breathing at " + currentRpm + " breaths per minute. That's some good breathing";
                }
            }
        }
        builder.setContentTitle(titleText)
               .setContentText(messageText)
               .setStyle(new Notification.BigTextStyle().bigText(messageText))
               .setOngoing(true)
               .build();
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    private void stopNotification() {
        this.notificationManager.cancel(NOTIFICATION_ID);

    }


    private Notification.Action getHelpAction() {
        return new Notification.Action.Builder(null,
                                               "Send help!",
                                               getPendingIntent(getOkIntent(false))).build();
    }


    private Notification.Action getOkAction() {
        return new Notification.Action.Builder(null,
                                               "False alarm",
                                               getPendingIntent(getOkIntent(true))).build();
    }

    private PendingIntent getPendingIntent(@NonNull final Intent intent) {

        return PendingIntent.getActivity(
                this,
                REQUEST_DEATH,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getOkIntent(final boolean isOk) {
        final Intent resultIntent = new Intent(this, SettingsActivity.class);
        resultIntent.putExtra(EXTRA_OK, isOk);
        return resultIntent;
    }
}

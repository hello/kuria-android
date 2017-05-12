package is.hellos.demos.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import is.hellos.demos.R;
import is.hellos.demos.activities.RespirationActivity;
import is.hellos.demos.interactors.v1.RespirationStatsInteractor;
import is.hellos.demos.models.respiration.RespirationStat;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;

/**
 * Created by simonchen on 5/11/17.
 */

public class RespirationWidgetService extends IntentService implements Observer {

    public static final String EXTRA_APP_WIDGET_IDS = RespirationWidgetService.class.getSimpleName()+"_EXTRA_APP_WIDGET_IDS";
    private int[] appWidgetIds;
    private RespirationStatsInteractor respirationStatsInteractor;
    private ZeroMQSubscriber subscriber;

    public RespirationWidgetService() {
        this(RespirationWidgetService.class.getSimpleName()+"_workerThread");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RespirationWidgetService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        this.appWidgetIds = intent.getIntArrayExtra(EXTRA_APP_WIDGET_IDS);
        this.respirationStatsInteractor = new RespirationStatsInteractor();
        respirationStatsInteractor.addObserver(this);
        this.subscriber = new ZeroMQSubscriber(ZeroMQSubscriber.RESPIRATION_STATS_TOPIC);
        subscriber.setListener(respirationStatsInteractor);
        subscriber.run();
    }

    @Override
    public void update(Observable o, Object arg) {
        final RespirationStat respirationStat = (RespirationStat) arg;
        final String lastBPM;
        final CharSequence lastMessage;
        @ColorInt
        final int lastBPMColor;
        if (respirationStat.isHasRespiration()) {
            lastBPM = String.format(Locale.US, "%.0f", (respirationStat.getBreathsPerMinute()));
            lastMessage = "Conner is still doing well. Don't worry we got your back :D";
            lastBPMColor = ContextCompat.getColor(this, R.color.success);
        } else {
            lastBPM = "--";
            lastMessage = "Conner's respiration was not detected recently. Please check up on him.";
            lastBPMColor = ContextCompat.getColor(this, R.color.error);
        }
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent launchIntent = new Intent(this, RespirationActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.respiration_appwidget);
            views.setOnClickPendingIntent(R.id.respiration_appwidget_view, pendingIntent);
            views.setTextViewText(R.id.respiration_appwidget_bpm, lastBPM);
            views.setTextColor(R.id.respiration_appwidget_bpm, lastBPMColor);
            views.setTextViewText(R.id.respiration_appwidget_message, lastMessage);

            // Tell the AppWidgetManager to perform an update on the current app widget
            AppWidgetManager.getInstance(this).updateAppWidget(appWidgetId, views);
        }
        finish();
    }

    private void finish() {
        appWidgetIds = null;
        respirationStatsInteractor.removeObserver(this);
        subscriber.stop();
        respirationStatsInteractor = null;
        subscriber = null;
    }
}

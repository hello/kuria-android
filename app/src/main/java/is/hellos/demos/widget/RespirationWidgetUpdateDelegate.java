package is.hellos.demos.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
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
 * Created by simonchen on 5/12/17.
 */

public class RespirationWidgetUpdateDelegate implements Observer{
    private final Context context;
    private final RespirationStatsInteractor respirationStatsInteractor;
    private int[] appWidgetIds;
    private ZeroMQSubscriber subscriber;

    public RespirationWidgetUpdateDelegate(@NonNull final Context context) {
        this.context = context;
        this.respirationStatsInteractor = new RespirationStatsInteractor();
        respirationStatsInteractor.addObserver(this);
        this.appWidgetIds = new int[]{};
    }

    /**
     * Will perform update on same thread as called. If async needed call inside background thread.
     * @param appWidgetIds
     */
    void startSingleUpdate(final int[] appWidgetIds) {
        this.appWidgetIds = appWidgetIds;
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
            lastBPMColor = ContextCompat.getColor(context, R.color.success);
        } else {
            lastBPM = "--";
            lastMessage = "Conner's respiration was not detected recently. Please check up on him.";
            lastBPMColor = ContextCompat.getColor(context, R.color.error);
        }
        // Perform this loop procedure for each App Widget
        for (int i=0; i<appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent launchIntent = new Intent(context, RespirationActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.respiration_appwidget);
            views.setOnClickPendingIntent(R.id.respiration_appwidget_view, pendingIntent);
            views.setTextViewText(R.id.respiration_appwidget_bpm, lastBPM);
            views.setTextColor(R.id.respiration_appwidget_bpm, lastBPMColor);
            views.setTextViewText(R.id.respiration_appwidget_message, lastMessage);

            // Tell the AppWidgetManager to perform an update on the current app widget
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
        }
        finish();
    }

    void finish() {
        appWidgetIds = null;
        respirationStatsInteractor.removeObserver(this);
        if (this.subscriber != null) {
            subscriber.stop();
            subscriber = null;
        }
    }
}

package is.hellos.demos.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by simonchen on 5/11/17.
 */

public class RespirationWidgetProvider extends AppWidgetProvider {
    /**
     * Not called if configuration activity defined
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final Intent intentService = new Intent(context, RespirationWidgetService.class);
        intentService.putExtra(RespirationWidgetService.EXTRA_APP_WIDGET_IDS, appWidgetIds);
        final PendingIntent pendingIntentService = PendingIntent.getService(context, 0, intentService, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntentService);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}

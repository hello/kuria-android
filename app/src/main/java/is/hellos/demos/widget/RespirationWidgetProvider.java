package is.hellos.demos.widget;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

/**
 * Created by simonchen on 5/11/17.
 */

public class RespirationWidgetProvider extends AppWidgetProvider {
    private static final int RESPIRATION__SERVICE_JOB_ID = 0xdeadbeef;

    /**
     * Not called if configuration activity defined
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        /*
        final Intent intentService = new Intent(context, RespirationWidgetService.class);
        intentService.putExtra(RespirationWidgetService.EXTRA_APP_WIDGET_IDS, appWidgetIds);
        final PendingIntent pendingIntentService = PendingIntent.getService(context, 0, intentService, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntentService);*/

        final ComponentName componentName = new ComponentName(context, RespirationWidgetJobService.class);
        PersistableBundle bundleExtras = new PersistableBundle();
        bundleExtras.putIntArray(RespirationWidgetJobService.EXTRA_APP_WIDGET_IDS, appWidgetIds);
        JobInfo updateAppWidgetJob = new JobInfo.Builder(RESPIRATION__SERVICE_JOB_ID, componentName)
                .setPeriodic(10000) /* For Android 25 and up clamped to min of 15 minutes same problem as alarm manager */
                .setExtras(bundleExtras)
                .build();
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(updateAppWidgetJob);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(RESPIRATION__SERVICE_JOB_ID);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}

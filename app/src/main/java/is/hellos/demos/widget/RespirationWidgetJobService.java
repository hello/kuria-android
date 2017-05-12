package is.hellos.demos.widget;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

/**
 * Created by simonchen on 5/12/17.
 */

public class RespirationWidgetJobService extends JobService {
    public static final String EXTRA_APP_WIDGET_IDS = RespirationWidgetJobService.class.getSimpleName() + "_extra_app_widget_ids";

    private final RespirationWidgetUpdateDelegate delegate;
    private AsyncTask singleRespirationUpdateTask;

    public RespirationWidgetJobService() {
        this.delegate = new RespirationWidgetUpdateDelegate(this);
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        final int[]appWidgetIds = params.getExtras().getIntArray(EXTRA_APP_WIDGET_IDS);
        this.singleRespirationUpdateTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] ignore) {
                RespirationWidgetJobService.this.delegate.startSingleUpdate(appWidgetIds);
                RespirationWidgetJobService.this.jobFinished(params, false);
                return null;
            }
        };
        this.singleRespirationUpdateTask.execute();
        return true /* hasOngoingWork required in separate thread */;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (this.singleRespirationUpdateTask != null
                && !this.singleRespirationUpdateTask.isCancelled()) {
            this.singleRespirationUpdateTask.cancel(true);
        }
        this.delegate.finish();
        return false;
    }
}

package is.hellos.demos.widget;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by simonchen on 5/11/17.
 */

public class RespirationWidgetService extends IntentService {

    public static final String EXTRA_APP_WIDGET_IDS = RespirationWidgetService.class.getSimpleName()+"_EXTRA_APP_WIDGET_IDS";
    private final RespirationWidgetUpdateDelegate delegate;

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
        this.delegate = new RespirationWidgetUpdateDelegate(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        final int[] appWidgetIds = intent.getIntArrayExtra(EXTRA_APP_WIDGET_IDS);
        this.delegate.startSingleUpdate(appWidgetIds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.delegate.finish();
    }
}

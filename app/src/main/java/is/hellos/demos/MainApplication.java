package is.hellos.demos;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import is.hellos.demos.activities.SettingsActivity;
import is.hellos.demos.broadcastreceivers.NotificationBroadcastReceiver;
import is.hellos.demos.network.ApiService;
import retrofit2.Retrofit;

public class MainApplication extends Application {

    private static final String PREF_NAME = SettingsActivity.class.getSimpleName() + ".PREF_NAME";
    private static final String KEY_DEATH_NOTIFICATIONS = SettingsActivity.class.getSimpleName() + ".KEY_DEATH_NOTIFICATIONS";
    private static final String KEY_CRYING_NOTIFICATIONS = SettingsActivity.class.getSimpleName() + ".KEY_CRYING_NOTIFICATIONS";
    @Nullable
    private ApiService apiService = null;

    @Nullable
    private SharedPreferences sharedPreferences;


    @NonNull
    public ApiService getApiService() {
        if (this.apiService == null) {
            this.apiService = new Retrofit.Builder()
                    .baseUrl("todo") //todo update
                    .build()
                    .create(ApiService.class);
        }
        return this.apiService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LocalBroadcastManager.getInstance(this).registerReceiver(new NotificationBroadcastReceiver(),
                                                                 NotificationBroadcastReceiver.getIntentFilter());

       /*
        final String TAG = MainApplication.class.getSimpleName();
        final ZeroMQSubscriber babyStateSubscriber = ZeroMQSubscriber.getBabyStateSubscriber();
        babyStateSubscriber.setListener(new BabyStateInteractor(this));

        final HandlerThread handlerThread = new HandlerThread(TAG, HandlerThread.MAX_PRIORITY);
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper());
        handler.post(babyStateSubscriber);*/
    }

    @NonNull
    public SharedPreferences getSharedPreferences() {
        if (this.sharedPreferences == null) {
            this.sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public boolean shouldNotifyDeath() {
        return getSharedPreferences().getBoolean(KEY_DEATH_NOTIFICATIONS, false);
    }

    public void setNotifyDeath(final boolean notify) {
        getSharedPreferences().edit().putBoolean(KEY_DEATH_NOTIFICATIONS, notify).apply();
    }

    public void setNotifyCrying(boolean notify) {
        getSharedPreferences().edit().putBoolean(KEY_CRYING_NOTIFICATIONS, notify).apply();
    }

    public boolean shouldNotifyCrying() {
        return getSharedPreferences().getBoolean(KEY_CRYING_NOTIFICATIONS, false);
    }
}

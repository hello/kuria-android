package is.hellos.demos;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import is.hellos.demos.broadcastreceivers.NotificationBroadcastReceiver;
import is.hellos.demos.interactors.BabyStateInteractor;
import is.hellos.demos.network.ApiService;
import is.hellos.demos.network.zmq.ZeroMQSubscriber;
import retrofit2.Retrofit;

public class MainApplication extends Application {

    @Nullable
    private ApiService apiService = null;


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

        final String TAG = MainApplication.class.getSimpleName();

        final ZeroMQSubscriber babyStateSubscriber = ZeroMQSubscriber.getBabyStateSubscriber();
        babyStateSubscriber.setListener(new BabyStateInteractor(this));

        final HandlerThread handlerThread = new HandlerThread(TAG, HandlerThread.MAX_PRIORITY);
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper());
        handler.post(babyStateSubscriber);

    }
}

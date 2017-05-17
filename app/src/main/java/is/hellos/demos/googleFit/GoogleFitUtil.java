package is.hellos.demos.googleFit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Device;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import is.hellos.demos.interactors.BaseZMQInteractor;

/**
 * Created by simonchen on 5/15/17.
 */

public class GoogleFitUtil {

    private static final String TAG = GoogleFitUtil.class.getSimpleName();

    private final Context context;
    private final GoogleApiClient client;
    private final FitConnectionFailedListener fitConnectionFailedListener;
    private final FitConnectionCallback fitConnectionCallback;
    private final FitSubscriptionCallback fitSubscriptionCallback;
    private final DataSource dataSource;

    public GoogleFitUtil(@NonNull final Context context) {
        this.context = context;
        this.fitConnectionCallback = new FitConnectionCallback();
        this.fitConnectionFailedListener = new FitConnectionFailedListener();
        this.fitSubscriptionCallback = new FitSubscriptionCallback();
        this.client = buildClient(fitConnectionCallback, fitConnectionFailedListener);
        this.dataSource = buildDataSource();
    }

    public void start(FitObserver observer) {
        this.fitConnectionFailedListener.addObserver(observer);
        this.fitConnectionCallback.addObserver(observer);
        this.fitSubscriptionCallback.addObserver(observer);
        if (!(client.isConnected() || client.isConnecting())) {
            client.connect();
        }
    }

    public void end() {
        if (client.isConnected() || client.isConnecting()) {
            unsubscribe();
            client.disconnect();
        }
        this.fitConnectionCallback.removeObservers();
        this.fitConnectionFailedListener.removeObservers();
        this.fitSubscriptionCallback.removeObservers();
    }

    public void disable() {
        Fitness.ConfigApi.disableFit(client).await(10, TimeUnit.SECONDS);
    }

    GoogleApiClient buildClient(@NonNull final GoogleApiClient.ConnectionCallbacks connectionCallback,
                                @NonNull final GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        return new GoogleApiClient.Builder(context)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SESSIONS_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.CONFIG_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(connectionCallback)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
    }

    DataSource buildDataSource() {
        final DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setStreamName("My Baby BPM Stream")
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setType(DataSource.TYPE_DERIVED)
                .setDevice(Device.getLocalDevice(context))
                .build();
        return dataSource;
    }

    //todo integrate with real data
    DataSet gatherDataPoints() {
        final DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint;
        int size = 0;
        while (size < 100) {
            dataPoint = dataSet.createDataPoint();
            dataPoint.setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            dataPoint.getValue(Field.FIELD_BPM).setInt(100);
            dataSet.add(dataPoint);
            size++;
        }
        return dataSet;
    }

    public void insertData() {
        Fitness.HistoryApi.insertData(client, gatherDataPoints()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.i(TAG, "data set inserted status " + status.toString());
            }
        });
    }

    public void subscribe() {
        Fitness.RecordingApi.subscribe(client, dataSource).setResultCallback(fitSubscriptionCallback);
    }

    public void unsubscribe() {
        Fitness.RecordingApi.unsubscribe(client, dataSource).await(10, TimeUnit.SECONDS);
    }

    private Session session() {
        final long startTimeMillis = System.currentTimeMillis();
        return new Session.Builder()
                .setName("Baby BPM Session" + new SimpleDateFormat("MM, dd, yyyy HH:mm:ss", Locale.US).format(new Date(startTimeMillis)))
                .setIdentifier("My Baby BPM Session")
                .setDescription("Real time breaths per minute session")
                .setStartTime(startTimeMillis, TimeUnit.MILLISECONDS)
                .build();
    }

    public void startSession() {
        Fitness.SessionsApi.startSession(client, session()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.i(TAG, "session start result status " + status.toString());
                if (status.isSuccess()) {
                    insertData();
                }
            }
        });
    }

    static abstract class FitObservable<T> {

        BaseZMQInteractor.BaseObservable<T> observable = new BaseZMQInteractor.BaseObservable<>();

        public void addObserver(@NonNull final Observer observer) {
            this.observable.addObserver(observer);
        }

        public void removeObservers() {
            this.observable.deleteObservers();
        }
    }

    static class FitConnectionCallback extends FitObservable<Bundle>
            implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "connected to client");
            this.observable.update(bundle);
        }

        @Override
        public void onConnectionSuspended(int i) {
            if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                Log.i(TAG, "Connection lost.  Cause: Network Lost.");
            } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
            }
        }
    }

    static class FitConnectionFailedListener extends FitObservable<ConnectionResult>
            implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i(TAG, "Connection failed. Reason: " + connectionResult);
            if (connectionResult.hasResolution()) {
                this.observable.update(connectionResult);
            }
        }
    }

    static class FitSubscriptionCallback extends FitObservable<Status> implements ResultCallback<Status>{
        @Override
        public void onResult(@NonNull Status status) {
            Log.i(TAG, "subscription status " + status.toString());
            this.observable.update(status);
        }
    }
}

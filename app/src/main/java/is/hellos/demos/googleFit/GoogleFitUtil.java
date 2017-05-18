package is.hellos.demos.googleFit;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
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
import com.google.android.gms.fitness.result.SessionStopResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import is.hellos.demos.interactors.BaseZMQInteractor;

/**
 * Created by simonchen on 5/15/17.
 */

public class GoogleFitUtil {

    private static final String TAG = GoogleFitUtil.class.getSimpleName();

    private final Context context;
    private final GoogleApiClient client;
    private final FitConnectionFailedCallback fitConnectionFailedCallback;
    private final FitConnectionCallback fitConnectionCallback;
    private final FitStatusCallback fitSubscriptionCallback;
    private final FitStatusCallback fitUnsubscribeCallback;
    private final FitStatusCallback fitSessionStartCallback;
    private final FitSessionStopCallback fitSessionStopCallback;

    private final DataSource dataSource;

    @Nullable
    private Session session;

    public GoogleFitUtil(@NonNull final Context context) {
        this.context = context;
        this.fitConnectionCallback = new FitConnectionCallback();
        this.fitConnectionFailedCallback = new FitConnectionFailedCallback();
        this.fitSubscriptionCallback = new FitStatusCallback(State.SUBSCRIBED);
        this.fitUnsubscribeCallback = new FitStatusCallback(State.UNSUBSCRIBED);
        this.fitSessionStartCallback = new FitStatusCallback(State.SESSION_STARTED);
        this.fitSessionStopCallback = new FitSessionStopCallback();
        this.client = buildClient(fitConnectionCallback, fitConnectionFailedCallback);
        this.dataSource = buildDataSource();
    }

    public void start(FitObserver observer) {
        this.fitConnectionFailedCallback.addObserver(observer);
        this.fitConnectionCallback.addObserver(observer);
        this.fitSubscriptionCallback.addObserver(observer);
        this.fitUnsubscribeCallback.addObserver(observer);
        this.fitSessionStartCallback.addObserver(observer);
        this.fitSessionStopCallback.addObserver(observer);
        if (!(client.isConnected() || client.isConnecting())) {
            client.connect();
        }
    }

    public void end() {
        if (client.isConnected() || client.isConnecting()) {
            unsubscribe();
            client.disconnect();
        }
    }

    public void removeObservers() {
        this.fitConnectionCallback.deleteObservers();
        this.fitConnectionFailedCallback.deleteObservers();
        this.fitSubscriptionCallback.deleteObservers();
        this.fitSessionStartCallback.deleteObservers();
        this.fitUnsubscribeCallback.deleteObservers();
        this.fitSessionStopCallback.deleteObservers();
    }

    public void disable() {
        Fitness.ConfigApi.disableFit(client);
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
            dataPoint.getValue(Field.FIELD_BPM).setFloat(100f); // will throw exception if wrong type of value set
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

    @RequiresPermission(allOf = {Manifest.permission.BODY_SENSORS})
    public void subscribe() {
        Fitness.RecordingApi.subscribe(client, dataSource).setResultCallback(fitSubscriptionCallback);
    }

    public void unsubscribe() {
        Fitness.RecordingApi.unsubscribe(client, dataSource).setResultCallback(fitUnsubscribeCallback);
    }

    private Session session() {
        final long startTimeMillis = System.currentTimeMillis();
        return new Session.Builder()
                .setName("Baby BPM Session")
                .setIdentifier("My Baby BPM Session" + new SimpleDateFormat("MM, dd, yyyy HH:mm:ss", Locale.US).format(new Date(startTimeMillis)))
                .setDescription("Real time breaths per minute session")
                .setStartTime(startTimeMillis, TimeUnit.MILLISECONDS)
                .build();
    }

    public void startSession() {
        this.session = session();
        Fitness.SessionsApi.startSession(client, session).setResultCallback(fitSessionStartCallback);
    }

    public void stopSession() {
        if (this.session == null) {
            Log.i(TAG, "session not started");
            return;
        }
        Fitness.SessionsApi.stopSession(client, session.getIdentifier()).setResultCallback(fitSessionStopCallback);
    }

    static abstract class FitObservable<T> extends BaseZMQInteractor.BaseObservable<Wrapper>{

        abstract State getTag();

        void wrapUpdate(T value) {
            super.update(new Wrapper(getTag(), value));
        }
    }

    static class Wrapper {
        final State TAG;
        final Object VALUE;

        Wrapper(State tag, Object value) {
            TAG = tag;
            VALUE = value;
        }
    }

    private static class FitConnectionCallback extends FitObservable<Bundle>
            implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "connected to client");
            wrapUpdate(bundle);
        }

        @Override
        public void onConnectionSuspended(int i) {
            if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                Log.i(TAG, "Connection lost.  Cause: Network Lost.");
            } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
            }
        }

        @Override
        State getTag() {
            return State.CONNECTED;
        }
    }

    private static class FitConnectionFailedCallback extends FitObservable<ConnectionResult>
            implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i(TAG, "Connection failed. Reason: " + connectionResult);
            if (connectionResult.hasResolution()) {
                wrapUpdate(connectionResult);
            }
        }

        @Override
        State getTag() {
            return State.CONNECTION_FAILED;
        }
    }

    private static class FitStatusCallback extends FitObservable<Status> implements ResultCallback<Status> {

        private final State state;

        FitStatusCallback(@NonNull final State state) {
            this.state = state;
        }

        @Override
        public void onResult(@NonNull Status status) {
            Log.i(TAG, "subscription status " + status.toString());
            wrapUpdate(status);
        }

        @Override
        State getTag() {
            return state;
        }
    }

    private static class FitSessionStopCallback extends FitObservable<Status> implements ResultCallback<SessionStopResult> {
        @Override
        public void onResult(@NonNull SessionStopResult sessionStopResult) {
            Log.i(TAG, "session stop result status " + sessionStopResult.toString());
            wrapUpdate(sessionStopResult.getStatus());
        }

        @Override
        State getTag() {
            return State.SESSION_STOPPED;
        }
    }
}

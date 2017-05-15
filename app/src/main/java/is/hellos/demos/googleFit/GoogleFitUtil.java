package is.hellos.demos.googleFit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Device;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

/**
 * Created by simonchen on 5/15/17.
 */

public class GoogleFitUtil {

    private static final String TAG = GoogleFitUtil.class.getSimpleName();

    private final Context context;
    private final GoogleApiClient client;

    public GoogleFitUtil(@NonNull final Context context) {
        this.context = context;
        this.client = buildClient(new FitConnectionCallback(), new FitConnectionFailedListener());
    }

    public void start() {
        if (!(client.isConnected() || client.isConnecting())) {
            client.connect();
        }
    }

    public void end() {
        if (client.isConnected() || client.isConnecting()) {
            client.disconnect();
        }
    }

    GoogleApiClient buildClient(@NonNull final GoogleApiClient.ConnectionCallbacks connectionCallback,
                                @NonNull final GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        return new GoogleApiClient.Builder(context)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(connectionCallback)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
    }

    void dataSource() {
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setStreamName("My Baby BPM Stream")
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setType(DataSource.TYPE_DERIVED)
                .setDevice(Device.getLocalDevice(context))
                .build();
        SensorRequest sensorRequest = new SensorRequest.Builder().setDataSource(dataSource).build();
        Fitness.SensorsApi.add(client, sensorRequest, new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {

            }
        });
    }

    static class FitConnectionCallback implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "connected to client");
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

    static class FitConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i(TAG, "Connection failed. Reason: " + connectionResult.getErrorMessage());
        }
    }
}

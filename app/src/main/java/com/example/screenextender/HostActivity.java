package com.example.screenextender;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.nearby.Nearby;
//import com.google.android.gms.nearby.connection.AdvertisingOptions;
//import com.google.android.gms.nearby.connection.ConnectionInfo;
//import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
//import com.google.android.gms.nearby.connection.ConnectionResolution;
//import com.google.android.gms.nearby.connection.Connections;
//import com.google.android.gms.nearby.connection.Strategy;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;




public class HostActivity extends AppCompatActivity {

    private ConnectionsClient connectionsClient;

    private static final String TAG = "screenExtender";


    public static final String CLIENT_NAME = "InfScreenHost";

    public static final String SERVICE_ID = "com.google.example.screenExtender";

    public static final com.google.android.gms.nearby.connection.Strategy STRATEGY = Strategy.P2P_STAR;

    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    }
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_host);


        connectionsClient = Nearby.getConnectionsClient(this);

        startAdvertising();

    }

    private void startAdvertising() {
        connectionsClient.startAdvertising(
                CLIENT_NAME, SERVICE_ID, connectionLifecycleCallback, new AdvertisingOptions(STRATEGY));
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");


                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                }

            };


}

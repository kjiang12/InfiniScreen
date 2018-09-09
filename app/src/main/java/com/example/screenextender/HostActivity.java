package com.example.screenextender;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.view.WindowManager;


import com.example.screenextender.clientmanager.ClientManagementActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class HostActivity extends AppCompatActivity {
    Message mMessage;

    Button allClientsJoinedBtn;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://infiniscreen.herokuapp.com");
        } catch (URISyntaxException e) {}
    }
    private ArrayList<DeviceInfo> clientsInfo = new ArrayList<>();

    private Emitter.Listener onJoinCodeReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int joinCode = (Integer)args[0];
                    sendMessage(String.valueOf(joinCode));
                }
            });
        }
    };

    private Emitter.Listener onClientListReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        JSONArray clients = data.getJSONArray("clients");
                        for (int i = 0; i < clients.length(); i++) {
                            JSONObject thisClient = (JSONObject) clients.get(i);
                            clientsInfo.add(new DeviceInfo(thisClient.getString("id"), thisClient.getString("name")));
                        }

                        int currposition = 1; // 0 to 3
                        ArrayList<DeviceGridPositionInfo.SingleDevicePosition> devicePositions = new ArrayList<>();
                        for (DeviceInfo currDevice : clientsInfo) {
                            DeviceGridPositionInfo.SingleDevicePosition currDevicePosition = new DeviceGridPositionInfo.SingleDevicePosition(currDevice.getId(), currposition/2, currposition % 2);
                            devicePositions.add(currDevicePosition);
                            currposition++;
                        }
                        /*
                        DeviceGridPositionInfo deviceGridPositionInfo = new DeviceGridPositionInfo(2, 2, devicePositions);
                        Gson gson = new Gson();
                        try {
                            JSONObject obj = new JSONObject(gson.toJson(deviceGridPositionInfo));
                            mSocket.emit("positions", obj);
                        } catch (JSONException e) {

                        }
                        */


                        Intent intent = new Intent(HostActivity.this, ClientManagementActivity.class);

                        Bundle b = new Bundle();
                        b.putParcelableArrayList("clientlist", clientsInfo);



                        intent.putExtras(b);
                        startActivity(intent);


                        Log.d("antli", "retrieved clients");
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

        mSocket.on("code_assignment", onJoinCodeReceived);
        mSocket.on("clients", onClientListReceived);
        mSocket.connect();
        mSocket.emit("host_start");
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_host_broadcasting);
        allClientsJoinedBtn = findViewById(R.id.client_join_finished_btn);
        allClientsJoinedBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSocket.emit("registration_complete");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(mMessage != null){
            Nearby.getMessagesClient(this).unpublish(mMessage);
        }
        super.onStop();
    }

    protected void sendMessage(String text){
        mMessage = new Message(text.getBytes());
        Nearby.getMessagesClient(this).publish(mMessage);
    }

    public static class DeviceInfo implements Parcelable {
        String mId;
        String mName;
        public DeviceInfo(String id, String name) {
            mId = id;
            mName = name;
        }
        public String getId() {
            return mId;
        }
        public String getName() {
            return mName;
        }

        // parceling

        public DeviceInfo(Parcel in) {
            this.mId = in.readString();
            this.mName = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mId);
            parcel.writeString(this.mName);
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator<DeviceInfo>() {
            @Override
            public DeviceInfo createFromParcel(Parcel in) {
                return new DeviceInfo(in);
            }

            public DeviceInfo[] newArray(int size) {
                return new DeviceInfo[size];
            }
        };
    }
}

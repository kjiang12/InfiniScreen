package com.example.screenextender;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.view.WindowManager;


import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private ArrayList<String> clientNames = new ArrayList<>();

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
                            clientNames.add(thisClient.getString("name"));
                        }
                        Log.d("antli", clientNames.toString());


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
}

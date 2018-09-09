package com.example.screenextender;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;


import java.net.URISyntaxException;


public class HostActivity extends AppCompatActivity {
    Message mMessage;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://infiniscreen.herokuapp.com");
        } catch (URISyntaxException e) {}
    }

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

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        mSocket.on("code_assignment", onJoinCodeReceived);
        mSocket.connect();
        mSocket.emit("host_start");
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_host_broadcasting);
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

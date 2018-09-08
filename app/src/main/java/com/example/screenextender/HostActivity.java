package com.example.screenextender;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;


public class HostActivity extends AppCompatActivity {
    Message mMessage;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
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

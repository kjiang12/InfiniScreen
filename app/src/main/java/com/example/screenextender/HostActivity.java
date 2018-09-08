package com.example.screenextender;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;


public class HostActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_host);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Message mMessage = new Message("TESTVOID".getBytes());
        Nearby.getMessagesClient(this).publish(mMessage);
    }
}

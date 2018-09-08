package com.example.screenextender;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

public class ClientActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_client);
    }

    MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onFound(Message message) {
            setCompleted(new String(message.getContent()));
        }

        @Override
        public void onLost(Message message) {
            setDisconnect();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    @Override
    protected void onStop() {
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
        super.onStop();
    }

    
    protected void setCompleted(String value){
        ProgressBar loadingBar = findViewById(R.id.join_loading);
        loadingBar.setVisibility(View.GONE);

        TextView description = findViewById(R.id.join_text);
        description.setText("Host Found!");

        TextView id = findViewById(R.id.id_text);
        id.setVisibility(View.VISIBLE);
        id.setText(value + "");

        final CardView card = findViewById(R.id.client_card);
        int colorFrom = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
        int colorTo = ContextCompat.getColor(this, android.R.color.holo_green_light);

        ValueAnimator colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                card.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    protected void setDisconnect(){
        ProgressBar loadingBar = findViewById(R.id.join_loading);
        loadingBar.setVisibility(View.VISIBLE);

        TextView description = findViewById(R.id.join_text);
        description.setText("Host Lost!");

        TextView id = findViewById(R.id.id_text);
        id.setVisibility(View.GONE);
        id.setText("");

        final CardView card = findViewById(R.id.client_card);
        int colorFrom = ContextCompat.getColor(this, android.R.color.holo_green_light);
        int colorTo = ContextCompat.getColor(this, android.R.color.holo_blue_light);

        ValueAnimator colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                card.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }
}

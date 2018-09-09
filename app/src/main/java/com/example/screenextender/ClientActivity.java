package com.example.screenextender;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.net.URISyntaxException;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

public class ClientActivity extends AppCompatActivity {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://infiniscreen.herokuapp.com");
        } catch (URISyntaxException e) {}
    }

    private Emitter.Listener onVideoPositionUrlReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    float xOrigin = parseArg(args[0]);
                    float yOrigin = parseArg(args[1]);
                    float width = parseArg(args[2]);
                    float height = parseArg(args[3]);
                    String url = (String)args[4];
                    Intent intent = new Intent(ClientActivity.this, VideoLoadActivity.class);
                    Bundle b = new Bundle();
                    b.putFloat("xOrigin", xOrigin);
                    b.putFloat("yOrigin", yOrigin);
                    b.putFloat("width", width);
                    b.putFloat("height", height);
                    b.putString("converted_url", url);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }
    };

    private float parseArg(Object argObject) {
        if (argObject instanceof Double) {

            double d = (Double) argObject;
            return (float)d;
        } else if (argObject instanceof Integer) {
            int i =  (Integer) argObject;
            return i;
        }
        else {
            return -1.0f;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSocket.on("positions_with_dl_url", onVideoPositionUrlReceived);
        mSocket.connect();
        setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_client);
    }

    MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onFound(Message message) {
            setCompleted(new String(message.getContent()));
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

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    protected void setCompleted(String value){
        ProgressBar loadingBar = findViewById(R.id.join_loading);
        loadingBar.setVisibility(View.GONE);

        TextView description = findViewById(R.id.join_text);
        description.setText("Host Found!");

        // Register this client with the server
        mSocket.emit("client_join", Integer.parseInt(value), getDeviceName());

        TextView id = findViewById(R.id.id_text);
        id.setVisibility(View.VISIBLE);
        id.setText(getDeviceName() + "");

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

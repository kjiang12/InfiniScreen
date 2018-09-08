package com.example.screenextender;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

public class ClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_client);

        Button button = findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCompleted(4);
            }
        });
    }


    protected void setCompleted(int value){
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
}

package com.example.screenextender;

import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

public class VideoCropAdminActivity extends VideoCropActivity implements TextureView.SurfaceTextureListener{

    private boolean playing = false;

    @Override
    void initView() {
        super.initView();
        FrameLayout rootView = (FrameLayout) findViewById(R.id.rootView);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:

                        if(!prepared) break;

                        if(!playing) {

                            mSocket.emit("play_command");

                        } else {
                            mSocket.emit("pause_command", mMediaPlayer.getCurrentPosition());
                        }

                        playing = !playing;


                        break;
                }
                return true;
            }
        });
    }
}

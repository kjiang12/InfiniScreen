package com.example.screenextender;


import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class VideoCropActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://infiniscreen.herokuapp.com");
        } catch (URISyntaxException e) {}
    }

    boolean prepared = false;

    private Emitter.Listener onPlayReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    syncPlay();
                }
            });
        }
    };

    private Emitter.Listener onPauseReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    syncPause((Integer)args[0]);
                }
            });
        }
    };

    // Log tag
    private static final String TAG = VideoCropActivity.class.getName();

    // Asset video file name
    private static final String FILE_NAME = "vid_source.mp4";

    // MediaPlayer instance to control playback of video file.
    MediaPlayer mMediaPlayer;
    private TextureView mTextureView;

    private float xOrigin = 1, yOrigin = 1, width = 1, height = 1; // Expressed in 0-1 ratio

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texture_video_crop);

        mSocket.on("play", onPlayReceived);
        mSocket.on("pause", onPauseReceived);
        mSocket.connect();

        Bundle b = getIntent().getExtras();
        xOrigin = b.getFloat("xOrigin");
        yOrigin = b.getFloat("yOrigin");
        width = b.getFloat("width");
        height = b.getFloat("height");

        initView();
    }

    void initView() {
        mTextureView = (TextureView) findViewById(R.id.textureView);
        // SurfaceTexture is available only after the TextureView
        // is attached to a window and onAttachedToWindow() has been invoked.
        // We need to use SurfaceTextureListener to be notified when the SurfaceTexture
        // becomes available.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, screenHeight));

        mTextureView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        updateCropToDim(xOrigin, yOrigin, width, height);
    }

    private void updateCropToDim(float cropOriginXRatio, float cropOriginYRatio, float cropWidthRatio, float cropHeightRatio) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        float scaleX = 1 / cropWidthRatio;
        float scaleY = 1 / cropHeightRatio;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleX, 0, 0);

        Matrix matrix2 = new Matrix();
        matrix2.setTranslate(-cropOriginXRatio * screenWidth, -cropOriginYRatio * screenHeight + (1 - scaleY / scaleX * screenHeight));

        matrix2.postConcat(matrix);

        mTextureView.setTransform(matrix2);

        Toast.makeText(this, "scaleX = " + scaleX + "scaleY = " + scaleY +
                "translateX = " + (-cropOriginXRatio * screenWidth) + "translateY = " + (-cropOriginYRatio * screenHeight), Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            // Make sure we stop video and release resources when activity is destroyed.
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        Surface surface = new Surface(surfaceTexture);

        File file0 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file1 = new File(file0, "Infiniscreen/vid.mp4");

        String path = file1.getAbsolutePath();

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer
                    .setDataSource(path);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(false);

            float leftVolume = 1, rightVolume = 1;

            if(xOrigin + width / 2 > 0.5) {
                leftVolume = 0;
            } else {
                rightVolume = 0;
            }

            mMediaPlayer.setVolume(leftVolume, rightVolume);

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer.prepareAsync();

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                   prepared = true;
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    // Pause and seek to location.
    public void syncPause(int ms) {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mMediaPlayer.seekTo(ms);
    }

    public void syncPlay() {
        mMediaPlayer.start();
    }
}

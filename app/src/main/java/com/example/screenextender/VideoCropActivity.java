package com.example.screenextender;


import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.IOException;
import java.net.URISyntaxException;

public class VideoCropActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{
    private float mVideoWidth;
    private float mVideoHeight;

    Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://infiniscreen.herokuapp.com");
        } catch (URISyntaxException e) {}
    }

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

    private void calculateVideoSize() {
        try {
            AssetFileDescriptor afd = getAssets().openFd(FILE_NAME);
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(
                    afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            String height = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            mVideoHeight = Float.parseFloat(height);
            mVideoWidth = Float.parseFloat(width);

        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (NumberFormatException e) {
            Log.d(TAG, e.getMessage());
        }
    }

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

        calculateVideoSize();
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
        matrix.setScale(scaleX, scaleY);

        Matrix matrix2 = new Matrix();
        matrix2.setTranslate(-cropOriginXRatio * screenWidth, -cropOriginYRatio * screenHeight);

        matrix2.postConcat(matrix);

        mTextureView.setTransform(matrix2);
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

        try {
            AssetFileDescriptor afd = getAssets().openFd(FILE_NAME);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer
                    .setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(true);

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
                    //mediaPlayer.start();
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

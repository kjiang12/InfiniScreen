package com.example.screenextender;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.File;
import java.net.URISyntaxException;

public class VideoLoadAdminActivity extends AppCompatActivity {

    private boolean oneIsDone = false;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://infiniscreen.herokuapp.com");
        } catch (URISyntaxException e) {}
    }

    private DownloadManager downloadManager;
    private BroadcastReceiver onComplete;
    private long refid;

    private Intent nextIntent;
    private Bundle nextBundle;

    private Emitter.Listener onAllReady = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(oneIsDone) {
                        startActivity(nextIntent);
                    } else {
                        oneIsDone = true;
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        //xOrigin = getIntent().getExtras().getFloat("xOrigin");
        //yOrigin = getIntent().getExtras().getFloat("yOrigin");

        mSocket.on("all_ready", onAllReady);


        String convertedUrl = getIntent().getExtras().getString("converted_url");

        nextIntent = new Intent(VideoLoadAdminActivity.this, VideoCropAdminActivity.class);

        nextBundle = new Bundle();
        nextBundle.putFloat("xOrigin", getIntent().getExtras().getFloat("xOrigin"));
        nextBundle.putFloat("yOrigin", getIntent().getExtras().getFloat("yOrigin"));
        nextBundle.putFloat("width", getIntent().getExtras().getFloat("width"));
        nextBundle.putFloat("height", getIntent().getExtras().getFloat("height"));

        nextIntent.putExtras(nextBundle);

        Uri downloadUri = Uri.parse(convertedUrl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Downloading Video");
        request.setVisibleInDownloadsUi(true);

        onComplete = new BroadcastReceiver() {

            public void onReceive(Context ctxt, Intent intent) {

                // get the refid from the download manager
                long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if(referenceId == refid) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(oneIsDone) {
                                startActivity(nextIntent);
                            } else {
                                oneIsDone = true;
                            }
                        }
                    });
                }
            }
        };

        // delete the previous video file
        File file0 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file1 = new File(file0, "Infiniscreen/vid.mp4");
        if(file1.exists()) {
            file1.delete();
        }


        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Infiniscreen/vid.mp4");

        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        refid = downloadManager.enqueue(request);

        setContentView(R.layout.content_video_load);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onComplete);
    }


}

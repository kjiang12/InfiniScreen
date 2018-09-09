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

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.File;
import java.net.URISyntaxException;

public class VideoLoadActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://infiniscreen.herokuapp.com");
        } catch (URISyntaxException e) {}
    }


    DownloadManager downloadManager;
    BroadcastReceiver onComplete;
    long refid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        //xOrigin = getIntent().getExtras().getFloat("xOrigin");
        //yOrigin = getIntent().getExtras().getFloat("yOrigin");

        String convertedUrl = getIntent().getExtras().getString("converted_url");

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
                            mSocket.emit("ready");

                            Intent intent = new Intent(VideoLoadActivity.this, VideoCropActivity.class);

                            Bundle b = new Bundle();
                            b.putFloat("xOrigin", getIntent().getExtras().getFloat("xOrigin"));
                            b.putFloat("yOrigin", getIntent().getExtras().getFloat("yOrigin"));
                            b.putFloat("width", getIntent().getExtras().getFloat("width"));
                            b.putFloat("height", getIntent().getExtras().getFloat("height"));

                            intent.putExtras(b);
                            startActivity(intent);
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

package com.example.screenextender.clientmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.screenextender.DeviceGridPositionInfo;
import com.example.screenextender.R;
import com.example.screenextender.VideoLoadAdminActivity;
import com.example.screenextender.clientmanager.clientgraph.GraphFragment;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ClientManagementActivity extends AppCompatActivity implements GraphFragment.OnFragmentInteractionListener, SourceSelectFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    protected Fragment graphFragment;
    protected Fragment sourceFragment;
    private Socket mSocket;
    private Intent intent;
    private Bundle intentBundle;
    private FloatingActionButton fab;

    {
        try {
            mSocket = IO.socket("http://infiniscreen.herokuapp.com");
        } catch (URISyntaxException e) {}
    }

    private Emitter.Listener onConvertedUrlReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String convertedUrl = (String)args[0];
                    intentBundle.putString("converted_url", convertedUrl);
                    intent.putExtras(intentBundle);
                    startActivity(intent);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove notification bar

        mSocket.on("dl_url", onConvertedUrlReceived);
        mSocket.connect();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_client_management);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        graphFragment = new GraphFragment();
        sourceFragment = new SourceSelectFragment();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setEnabled(false);
                GraphFragment graph = ((GraphFragment)graphFragment);
                String[] ids = graph.getIds();
                SourceSelectFragment source = (SourceSelectFragment)sourceFragment;
                String source_url = source.getSource();
                ArrayList<DeviceGridPositionInfo.SingleDevicePosition> devicePositions = new ArrayList<>();
                int numRows = graph.getNumRows();
                int numCols = graph.getNumCols();

                intentBundle = new Bundle();
                for (int i = 0; i<ids.length; i++) {
                    if (ids[i].equals("Host")) {
                        float width = 1.0f/numCols,
                              height = 1.0f/numRows;
                        intentBundle.putFloat("xOrigin", (i % numCols) * width);
                        intentBundle.putFloat("yOrigin", (i / numCols) * height);
                        intentBundle.putFloat("width", 1.0f/numCols);
                        intentBundle.putFloat("height", 1.0f/numRows);
                    } else {
                        DeviceGridPositionInfo.SingleDevicePosition currDevicePosition = new DeviceGridPositionInfo.SingleDevicePosition(ids[i], i / numCols, i % numCols);
                        devicePositions.add(currDevicePosition);
                    }
                }

                DeviceGridPositionInfo deviceGridPositionInfo = new DeviceGridPositionInfo(numRows, numCols, devicePositions);
                Gson gson = new Gson();
                try {
                    JSONObject obj = new JSONObject(gson.toJson(deviceGridPositionInfo));
                    mSocket.emit("positions_url", obj, source_url);
                } catch (JSONException e) {

                }
                //Intent intent = new Intent(ClientManagementActivity.this, VideoCropAdminActivity.class);
                intent = new Intent(ClientManagementActivity.this, VideoLoadAdminActivity.class);



                //Toast.makeText(getBaseContext(), "Play Source", Toast.LENGTH_SHORT).show();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("clientlist", getIntent().getExtras().getParcelableArrayList("clientlist"));
        graphFragment.setArguments(bundle);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_client_management, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return graphFragment;
                case 1:
                    return sourceFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}

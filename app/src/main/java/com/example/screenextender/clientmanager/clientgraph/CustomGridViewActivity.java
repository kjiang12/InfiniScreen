package com.example.screenextender.clientmanager.clientgraph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.screenextender.R;

import java.util.HashMap;

public class CustomGridViewActivity extends BaseAdapter {

    private Context mContext;
    private final String[] gridViewString;
    private final int[] gridViewImageId;
    private HashMap<Integer, TextView> textFields;
    private HashMap<String, Integer> selectedPhones;

    public CustomGridViewActivity(Context context, String[] gridViewString, int[] gridViewImageId) {
        mContext = context;
        this.gridViewImageId = gridViewImageId;
        this.gridViewString = gridViewString;
        textFields = new HashMap<Integer, TextView>();
        selectedPhones = new HashMap<String, Integer>();

    }

    @Override
    public int getCount() {
        return gridViewString.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridViewAndroid = new View(mContext);
            gridViewAndroid = inflater.inflate(R.layout.gridview_layout, null);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            textViewAndroid.setText(gridViewString[i]);
            imageViewAndroid.setImageResource(gridViewImageId[i]);
            if (textFields.containsKey(0) && i == 0) {

            } else {
                textFields.put(i, textViewAndroid);
            }
        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }

    public HashMap<Integer, TextView> getTextFields() {
        return textFields;
    }


    public HashMap<String, Integer> getSelectedPhones() {
        return selectedPhones;
    }

}
package com.example.screenextender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class GridViewImageTextActivity extends AppCompatActivity {

    GridView androidGridView;

    String[] gridViewString = {
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",

    } ;
    int[] gridViewImageId = {
            R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone,
            R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone,
            R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_image_text_example);

        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(GridViewImageTextActivity.this, gridViewString, gridViewImageId);
        androidGridView=(GridView)findViewById(R.id.grid_view_image_text);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Toast.makeText(GridViewImageTextActivity.this, "GridView Item: " + gridViewString[+i], Toast.LENGTH_LONG).show();
            }
        });

    }
}
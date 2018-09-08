package com.example.screenextender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Arrays;

import me.himanshusoni.quantityview.QuantityView;

public class GridViewImageTextActivity extends AppCompatActivity {

    GridView androidGridView;
    QuantityView rowsQuantityView;
    QuantityView colsQuantityView;
    String[] gridViewString;
    int[] gridViewImageId;
    CustomGridViewActivity adapterViewAndroid;
    /*
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


    String[] gridViewString = {
            "Alram", "Android",
            "Alram", "Android",

    } ;
    int[] gridViewImageId = {
            R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone,
            R.drawable.ic_horizontal_phone, R.drawable.ic_horizontal_phone,

    };
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_image_text_example);
        rowsQuantityView=(QuantityView)findViewById(R.id.quantityView_rows);
        colsQuantityView=(QuantityView)findViewById(R.id.quantityView_cols);
        generateGrid();

        QuantityView.OnQuantityChangeListener quantityChangeListener = new QuantityView.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int oldQuantity, int newQuantity, boolean programmatically) {
                generateGrid();
            }

            @Override
            public void onLimitReached() {

            }
        };
        rowsQuantityView.setOnQuantityChangeListener(quantityChangeListener);
        colsQuantityView.setOnQuantityChangeListener(quantityChangeListener);



        /*
        int numPhones = rowsQuantityView.getQuantity()*colsQuantityView.getQuantity();
        gridViewString = new String[numPhones];
        Arrays.fill(gridViewString, "Select...");
        gridViewImageId = new int[numPhones];
        Arrays.fill(gridViewImageId, R.drawable.ic_horizontal_phone);
        adapterViewAndroid = new CustomGridViewActivity(GridViewImageTextActivity.this, gridViewString, gridViewImageId);

        androidGridView.setNumColumns(colsQuantityView.getQuantity());
        androidGridView=(GridView)findViewById(R.id.grid_view_image_text);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Toast.makeText(GridViewImageTextActivity.this, "GridView Item: " + gridViewString[+i], Toast.LENGTH_LONG).show();
            }
        });
        */


    }

    private void generateGrid() {

        int numPhones = rowsQuantityView.getQuantity()*colsQuantityView.getQuantity();
        gridViewString = new String[numPhones];
        Arrays.fill(gridViewString, "Select...");
        gridViewImageId = new int[numPhones];
        Arrays.fill(gridViewImageId, R.drawable.ic_horizontal_phone);

        adapterViewAndroid = new CustomGridViewActivity(GridViewImageTextActivity.this, gridViewString, gridViewImageId);

        androidGridView=(GridView)findViewById(R.id.grid_view_image_text);
        androidGridView.setNumColumns(colsQuantityView.getQuantity());
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
package com.example.screenextender;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.himanshusoni.quantityview.QuantityView;

public class GridViewImageTextActivity extends AppCompatActivity {

    GridView androidGridView;
    QuantityView rowsQuantityView;
    QuantityView colsQuantityView;
    String[] gridViewString;
    int[] gridViewImageId;
    CustomGridViewActivity adapterViewAndroid;
    String[] phoneNames;
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
                //Toast.makeText(GridViewImageTextActivity.this, "GridView Item: " + gridViewString[+i], Toast.LENGTH_LONG).show();
                String[] testPhones = {"Test Phone 1", "Test Phone 2", "Test Phone 3"};
                showPhoneSelectionDialog(testPhones, i);
            }
        });
    }



    private void showPhoneSelectionDialog(final String[] phoneNames, final int index) {
        final Context context = this;
        this.runOnUiThread(new Runnable() {
            public void run()
            {

                final HashMap<Integer, TextView> textFields = adapterViewAndroid.getTextFields();
                new AlertDialog.Builder(context)
                        .setTitle("Pick phone to go in slot # "+(index + 1))
                        //.setMessage("Pick phone to go in slot # "+(index + 1))
                        .setItems(phoneNames, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(GridViewImageTextActivity.this, "GridView Item: " + index, Toast.LENGTH_LONG).show();

                                gridViewString[index] = phoneNames[which];
                                HashMap<Integer, TextView> textFields = adapterViewAndroid.getTextFields();
                                TextView newTextView = textFields.get(index);
                                newTextView.setText(phoneNames[which]);
                                textFields.put(index, newTextView);
                                Toast.makeText(GridViewImageTextActivity.this,  phoneNames[which] + adapterViewAndroid.getSelectedPhones().get(phoneNames[which]), Toast.LENGTH_LONG).show();
                                adapterViewAndroid.setTextFields(textFields);

                                if (adapterViewAndroid.getSelectedPhones().containsKey(phoneNames[which])) {
                                    HashMap<String, Integer> selectedPhones = adapterViewAndroid.getSelectedPhones();
                                    TextView selectTextView = textFields.get(selectedPhones.get(phoneNames[which]));
                                    selectTextView.setText("Selected...");
                                    textFields.put(index, selectTextView);
                                    adapterViewAndroid.setTextFields(textFields);
                                    selectedPhones.put(phoneNames[which], index);
                                    adapterViewAndroid.setSelectedPhones(selectedPhones);
                                } else {
                                    HashMap<String, Integer> selectedPhones = adapterViewAndroid.getSelectedPhones();
                                    selectedPhones.put(phoneNames[which], index);
                                    adapterViewAndroid.setSelectedPhones(selectedPhones);
                                }
                        /*for (Integer key : adapterViewAndroid.getTextFields().keySet()) {
                            if (adapterViewAndroid.getTextFields().get(key).getText().toString().equals(phoneNames[which]) && (key != index)) {
                                TextView selectTextView = adapterViewAndroid.getTextFields().get(index);
                                selectTextView.setText("Select...");
                                adapterViewAndroid.getTextFields().put(index, selectTextView);
                            }
                        }*/

                            }
                        })
//                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Log.d("MainActivity", "Sending atomic bombs to Jupiter");
//                        //adapterViewAndroid.getTextFields().get(index).setText(phoneNames[which]);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Log.d("MainActivity", "Aborting mission...");
//                        gridViewString[index] = "Select...";
//                        HashMap<String, Integer> selectedPhones = adapterViewAndroid.getSelectedPhones();
//                        HashMap<Integer, TextView> textFields = adapterViewAndroid.getTextFields();
//                        TextView newTextView = textFields.get(index);
//                        String text = newTextView.getText().toString();
//                        selectedPhones.remove(text);
//                        newTextView.setText("Select...");
//                        adapterViewAndroid.setSelectedPhones(selectedPhones);
//                    }
//                })
                        .show();
            }
        });


    };


}
package fr.benoitjaouen.sudoku;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class LevelActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        int level = getIntent().getIntExtra("level", 0);
        int resId = getResources().getIdentifier("raw/level" + level, null, this.getPackageName());
        InputStream is = this.getResources().openRawResource(resId);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String str = "";
        ArrayList<String> gridsModels = new ArrayList<>();
        if (is != null) {
            try {
                while ((str = reader.readLine()) != null) {
                    gridsModels.add(str);
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("Sudoku", MODE_PRIVATE);

       /* SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.commit();*/

        Grid[] gridList = new Grid[gridsModels.size()];
        for (int i = 0; i < gridsModels.size(); i++) {
            String json = sharedPreferences.getString("grid"+level+(i+1), "");
            Grid obj = new Gson().fromJson(json, Grid.class);
            if(obj != null){
                gridList[i] = obj;
            }else {
                gridList[i] = new Grid(level, i + 1, new Random().nextInt(100), gridsModels.get(i), null, null);
            }
        }
        final ArrayAdapter<Grid> adapter = new ArrayAdapter<Grid>(LevelActivity.this,
                android.R.layout.simple_list_item_2, android.R.id.text1, gridList) {
            @SuppressLint("ResourceType")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView title = view.findViewById(android.R.id.text1);
                TextView done = view.findViewById(android.R.id.text2);
                title.setText(getItem(position).getNumber() + " "+getString(R.string.level)+":" + getItem(position).getLevel());
                int percentageDone = getItem(position).getPercentageDone();
                done.setText(percentageDone + "%" + (getItem(position).getTimeDone() != null ? " " + getItem(position).getTimeDone() : ""));
                done.setTextColor(Color.parseColor(percentageDone < 50 ? getString(R.color.colorBad) : getString(R.color.colorGood)));
                return view;
            }
        };

        final ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        final LevelActivity context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                final Grid selectedGrid = (Grid) listView.getItemAtPosition(position);
                AlertDialog alertDialog = new AlertDialog.Builder(LevelActivity.this).create();
                alertDialog.setTitle("information");
                alertDialog.setMessage(selectedGrid.getNumber() + " -- " + selectedGrid.getPercentageDone() + "%"+ (selectedGrid.getTimeDone() != null ? " " + selectedGrid.getTimeDone() : ""));
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Continue..", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, SudokuActivity.class);
                        intent.putExtra("grid", selectedGrid);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }
        });
    }
}

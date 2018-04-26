package fr.benoitjaouen.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = findViewById(R.id.mainLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            Button button = new Button(this);
            button.setText(getString(R.string.level) + " " + (count + 1));
            linearLayout.addView(button, layoutParams);
            button.setId(count + 1);
            button.setOnClickListener(this);
            System.out.println("Raw Asset: " + fields[count].getName());
        }
    }

    @Override
    public void onClick(View view) {
        int levelValue = view.getId();
        Intent mIntent = new Intent(this, LevelActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putInt("level", levelValue);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }
}

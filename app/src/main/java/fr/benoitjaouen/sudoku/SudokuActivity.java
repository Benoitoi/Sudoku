package fr.benoitjaouen.sudoku;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SudokuActivity extends Activity implements TextWatcher {

    private String[][] board = new String[9][9];
    private Button buttonValidate;
    private Chronometer chronometer;
    private EditText[][] squares = new EditText[9][9];
    private final SudokuActivity context = this;
    private Grid grid;
    private String modelAnswer = "";
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        buttonValidate = findViewById(R.id.buttonValidate);

        grid = (Grid) getIntent().getSerializableExtra("grid");
        String model = grid.getModel();

        buttonValidate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isPlaying) {
                    showDialogMessage(validateGrid());
                } else {
                    buttonValidate.setText(getString(R.string.validate));
                    chronometer.start();
                    chronometer.setVisibility(View.VISIBLE);
                    isPlaying = true;
                    initGrid(grid.getModel());
                    refresh();
                }
            }
        });

        chronometer = findViewById(R.id.chronometer);
        if (grid.getTimeDone() != null) {
            buttonValidate.setText(getString(R.string.replay));
            chronometer.setVisibility(View.INVISIBLE);
        } else {
            isPlaying = true;
            chronometer.start();
        }
        initGrid(model);
    }

    @SuppressLint("ResourceType")
    private void initGrid(String model) {
        String[] splitedModel = model.split("");
        String[] splitedModelAnswer = null;
        if (grid.modelAnswer != null && !isPlaying) {
            splitedModelAnswer = grid.getModelAnswer().split("");
        }
        int count = 1;
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                String squareID = "square" + i + "" + j;
                String value = splitedModel[count];
                board[i - 1][j - 1] = value;
                System.out.println("case " + squareID);
                int resID = getResources().getIdentifier(squareID, "id", getPackageName());
                EditText square = findViewById(resID);
                System.out.println("valeur " + splitedModel[count]);
                System.out.println(count);
                squares[i - 1][j - 1] = square;
                if (!value.equals("0")) {
                    square.setText(value);
                    square.setEnabled(false);
                } else {
                    if (splitedModelAnswer != null) {
                        square.setText(splitedModelAnswer[count]);
                        board[i - 1][j - 1] = splitedModelAnswer[count];
                    }else {
                        square.setText("");
                    }
                    square.setTextColor(Color.parseColor(getString(R.color.colorCustom)));
                    square.addTextChangedListener(this);
                }
                count++;
            }
        }
    }

    @SuppressLint("ResourceType")
    private int validateGrid() {
        int status = 2;
        String[][] squareGroups = new String[3][3];
        String lineX = "";
        String lineY = "";
        for (int i = 0; i < 9; i++) {
            lineX = "";
            lineY = "";
            for (int j = 0; j < 9; j++) {
                if (board[i][j].equals("0") || board[i][j].equals("")) {
                    System.out.println("error at " + i + " " + j);
                    squares[i][j].requestFocus();
                    return 0;
                } else {
                    if (lineX.contains(board[i][j])) {
                        System.out.println("error at " + i + " " + j);
                        squares[i][j].setTextColor(Color.parseColor(getString(R.color.colorBad)));
                        squares[i][j].requestFocus();
                        return 1;
                    } else {
                        lineX += board[i][j];
                    }
                }
                if (board[j][i].equals("0") || board[j][i].equals("")) {
                    System.out.println("error at " + j + " " + i);
                    squares[j][i].requestFocus();
                    return 0;
                } else {
                    if (lineY.contains(board[j][i])) {
                        System.out.println("error at " + j + " " + i);
                        squares[j][i].setTextColor(Color.parseColor(getString(R.color.colorBad)));
                        squares[j][i].requestFocus();
                        return 1;
                    } else {
                        lineY += board[j][i];
                    }
                }

                System.out.println((i + 1) + " " + (j + 1));
                double posX = ((double) i + 1) / 3d;
                double posY = ((double) j + 1) / 3d;

                int indexX = -1;
                int indexY = -1;

                if (posX <= 1) {
                    indexX = 0;
                } else if (posX <= 2) {
                    indexX = 1;
                } else {
                    indexX = 2;
                }
                if (posY <= 1) {
                    indexY = 0;
                } else if (posY <= 2) {
                    indexY = 1;
                } else {
                    indexY = 2;
                }
                System.out.println(posX + " " + posY + " " + indexX + " " + indexY);
                squareGroups[indexX][indexY] = squareGroups[indexX][indexY] == null ? "" : squareGroups[indexX][indexY];
                squareGroups[indexX][indexY] += board[i][j];
            }
        }

        //TODO : trouver ou se trouve l"erreur
        String squareLine = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squareLine = "";
                String[] splitedValues = squareGroups[i][j].split("");
                for (int l = 1; l <= 9; l++) {
                    System.out.println("value " + splitedValues[l]);
                    System.out.println("line " + squareLine);
                    if (squareLine.contains(splitedValues[l])) {
                        System.out.println("error at " + i + " " + j);
                        return 1;
                    } else {
                        squareLine += splitedValues[l];
                    }
                }

            }
        }
        return status;
    }

    private void showDialogMessage(final int status) {
        long timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();

        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        final int seconds = (int) (elapsedMillis / 1000) % 60;
        final int minutes = (int) ((elapsedMillis / (1000 * 60)) % 60);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setIcon(android.R.drawable.ic_dialog_alert);
        String message = "";
        if (status == 2) {
            message = "Grille terminée en " + minutes + "'" + seconds + "\"";
            builder.setIcon(android.R.drawable.ic_dialog_info);
        } else if (status == 1) {
            message = "Grille incorrecte";
        } else if (status == 0) {
            message = "Grille incomplete";
        }

        builder.setTitle("Sodoku")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (status == 2) {
                            grid.setPercentageDone(100);
                            grid.setTimeDone(minutes + "'" + seconds + "\"");
                            grid.setModelAnswer(modelAnswer);
                            save();
                            Intent mIntent = new Intent(context, LevelActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putInt("level", grid.getLevel());
                            mIntent.putExtras(mBundle);
                            startActivity(mIntent);
                        }
                    }
                })
                .show();
    }

    private void save() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sudoku", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(grid);
        prefsEditor.putString("grid" + grid.getLevel() + grid.getNumber(), json);
        prefsEditor.commit();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Quitter")
                .setMessage("Êtes vous sûr de quitter la partie?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        refresh();
    }

    @SuppressLint("ResourceType")
    private void refresh(){
        if(isPlaying){
            for (int k = 0; k < 9; k++) {
                for (int l = 0; l < 9; l++) {
                    board[k][l] = squares[k][l].getText().toString();
                    modelAnswer += board[k][l];
                    if (squares[k][l].getText().toString().equals("")) {
                        squares[k][l].setTextColor(Color.parseColor(getString(R.color.colorCustom)));
                    }
                    System.out.println(board[k][l]);
                }
            }
        }
    }
    @Override
    public void afterTextChanged(Editable editable) {
    }
}

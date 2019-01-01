package com.waki.ezpzmath;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModesActivity extends AppCompatActivity {
    private Button first_mode_button;
    private Button second_mode_button;
    private Button third_mode_button;
    private ImageButton score_button;
    private ImageButton Settings_button;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        first_mode_button = findViewById(R.id.button1);
        first_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"+", "-"};
                openLevelsActivity(operators);
            }
        });

        second_mode_button = findViewById(R.id.button2);
        second_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"*", "/"};
                openLevelsActivity(operators);
            }
        });

        third_mode_button = findViewById(R.id.button3);
        third_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"+", "-", "*", "/"};
                openLevelsActivity(operators);

            }
        });

        score_button = findViewById(R.id.imageButton10);
        score_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openScoreActivity();
            }
        });

        Settings_button = findViewById(R.id.imageButton9);
        Settings_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openSettingsActivity();
            }
        });
        if(user != null) {
            Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openLevelsActivity(String[] operators){
        Intent intent = new Intent (this, LevelsActivity.class);
        intent.putExtra("operators", operators);
        startActivity(intent);
    }

    public void openScoreActivity(){
        Intent intent = new Intent (this, ScoreActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
        alertbox.getWindow().setBackgroundDrawableResource(R.color.dialog_color);
        alertbox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#c5f5c2"));
        alertbox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#c5f5c2"));
    }
}

package com.waki.ezpzmath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class LevelsActivity extends AppCompatActivity {

    private ImageButton Level_back_button;
    String[] operators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        operators = getIntent().getExtras().getStringArray("operators");
        Level_back_button = findViewById(R.id.imageButton9);
        Level_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModesActivity();
            }
        });

        Button EasyMode = findViewById(R.id.button1);
        EasyMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameActivity(1);
            }
        });

        Button NormalMode = findViewById(R.id.button2);
        NormalMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameActivity(2);
            }
        });

        Button HardMode = findViewById(R.id.button3);
        HardMode.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openGameActivity(3);
            }
        }));


    }

    @Override
    public void onBackPressed() {
        if (true) {
            openModesActivity();
        } else {
            super.onBackPressed();
        }
    }

    public void openModesActivity(){
        Intent intent = new Intent(this, ModesActivity.class);
        startActivity(intent);
    }
    public void openGameActivity(int difficulty)
    {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("operators", operators);
        startActivity(intent);
    }
}

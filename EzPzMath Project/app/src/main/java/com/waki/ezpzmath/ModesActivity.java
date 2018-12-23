package com.waki.ezpzmath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageButton;

public class ModesActivity extends AppCompatActivity {
    private Button first_mode_button;
    private Button second_mode_button;
    private Button third_mode_button;
    private ImageButton score_button;
    private ImageButton Settings_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        first_mode_button = (Button) findViewById(R.id.button1);
        first_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openLevelsActivity();
            }
        });

        second_mode_button = (Button) findViewById(R.id.button2);
        second_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openLevelsActivity();
            }
        });

        third_mode_button = (Button) findViewById(R.id.button3);
        third_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openLevelsActivity();

            }
        });

        score_button = (ImageButton) findViewById(R.id.imageButton10);
        score_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openScoreActivity();
            }
        });

        Settings_button = (ImageButton) findViewById(R.id.imageButton9);
        Settings_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openSettingsActivity();
            }
        });
    }

    public void openLevelsActivity(){
        Intent intent = new Intent (this, LevelsActivity.class);
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
}

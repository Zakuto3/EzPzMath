package com.waki.ezpzmath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        first_mode_button = (Button) findViewById(R.id.button1);
        first_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"+", "-"};
                openLevelsActivity(operators);
            }
        });

        second_mode_button = (Button) findViewById(R.id.button2);
        second_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"*", "/"};
                openLevelsActivity(operators);
            }
        });

        third_mode_button = (Button) findViewById(R.id.button3);
        third_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"+", "-", "*", "/"};
                openLevelsActivity(operators);

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
}

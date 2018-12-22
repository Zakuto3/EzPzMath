package com.waki.ezpzmath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;

public class LevelsActivity extends AppCompatActivity {

    private ImageButton Level_back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        Level_back_button = (ImageButton) findViewById(R.id.imageButton9);
        Level_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModesActivity();
            }
        });
    }
    public void openModesActivity(){
        Intent intent = new Intent(this, ModesActivity.class);
        startActivity(intent);
    }
}

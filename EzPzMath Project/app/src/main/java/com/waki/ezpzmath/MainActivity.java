package com.waki.ezpzmath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private Button Guest_button;
    private Button Login_button;
    private ImageButton exit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Guest_button = (Button) findViewById(R.id.button3);
        Guest_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openModesActivity();
            }
        });

        Login_button = (Button) findViewById(R.id.button4);
        Login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openModesActivity();
            }
        });

        exit_button = (ImageButton) findViewById(R.id.imageButton9);
        exit_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                finish();

            }
        });
    }

    public void openModesActivity(){
        Intent intent =  new Intent(this, ModesActivity.class);
        startActivity(intent);
    }
}

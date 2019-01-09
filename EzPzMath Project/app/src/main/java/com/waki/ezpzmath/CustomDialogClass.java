package com.waki.ezpzmath;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialogClass extends Dialog {

    public Activity c;
    public int button_clicked = 2;
    public Dialog d;
    public ImageButton home, again;
    int seconds = 0, minutes = 0, hours = 0;
    String [] operators;
    int difficulty;
    TextView time;
    boolean isPlaying;

    public CustomDialogClass(Activity a, int seconds,int minutes,int hours, String [] operators, int difficulty, boolean isPlaying) {
        super(a);
        this.c = a;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.operators = operators;
        this.difficulty = difficulty;
        this.isPlaying = isPlaying;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);
        home = (ImageButton) findViewById(R.id.btn_home);
        again = (ImageButton) findViewById(R.id.btn_again);
        time = (TextView) findViewById(R.id.time);
        time.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, ModesActivity.class); //finish the game activity and go to mode activity
                intent.putExtra("isPlaying", isPlaying);
                c.startActivity(intent);
                dismiss();
                c.finish();
            }
        });
        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, GameActivity.class); //make new gameactivity with same mode and difficulty
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("operators", operators);
                intent.putExtra("isPlaying", isPlaying);
                c.startActivity(intent);
                dismiss();
                c.finish();
            }
        });
    }
}


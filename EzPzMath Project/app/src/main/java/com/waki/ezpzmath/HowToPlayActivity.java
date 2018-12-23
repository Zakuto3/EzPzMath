package com.waki.ezpzmath;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.FontsContract;
import android.support.annotation.FontRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class HowToPlayActivity extends AppCompatActivity {
    ImageButton back_button;
    TextSwitcher textSwitcher;
    Button toRight;
    Button toLeft;
    String [] Pages = {"First Page :)","Second Page ;)","Third Page :D"};
    int currentIndex = -1;
    int pageCount = Pages.length;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_how_to_play);
        back_button = (ImageButton) findViewById(R.id.backButton_howtoplay);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });
        textSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public View makeView() {
                TextView text1 = new TextView(HowToPlayActivity.this);
                text1.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                text1.setTextSize(20);
                Typeface type = getResources().getFont(R.font.joyfultheatre);
                text1.setTypeface(type);
                text1.setTextColor(Color.parseColor("#266352"));
                return text1;
            }
        });
        Animation right =AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        Animation left = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);
        textSwitcher.setAnimation(right);
        textSwitcher.setAnimation(left);


        textSwitcher.setText("Use navigation buttons to navigate between the pages...");
        toRight=(Button) findViewById(R.id.right_button);
        toLeft = (Button) findViewById(R.id.left_button);

        toRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex++;
                if(currentIndex < pageCount){
                    textSwitcher.setText(Pages[currentIndex]);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Last Page",Toast.LENGTH_SHORT).show();
                    currentIndex--;
                }

            }
        });
        toLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex--;
                if(currentIndex >= 0 && currentIndex <= pageCount)
                {
                    textSwitcher.setText(Pages[currentIndex]);
                }
                else {
                    currentIndex++;
                }
            }
        });


    }
    public void openSettingsActivity(){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }
}

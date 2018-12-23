package com.waki.ezpzmath;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ScoreActivity extends AppCompatActivity {

    TabLayout score;
    int currentScoreTab = 0;
    ArrayAdapter<String> modesAdapter;
    Spinner modeSpin;
    ArrayAdapter<String> levelAdapter;
    Spinner levelSpin;
    private ImageButton score_back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score_back_button = (ImageButton) findViewById(R.id.imageButton9);
        score_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModesActivity();
            }
        });
    }

    public void openModesActivity(){
        Intent intent = new Intent (this, ModesActivity.class);
        startActivity(intent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        setScoreListener();
        levelSpin = findViewById(R.id.spinner2);
        String[] levels = new String[] {"Easy", "Normal", "Hard"};
        levelAdapter = new ArrayAdapter<String>(this, R.layout.score_spinner_text, levels);
        levelAdapter.setDropDownViewResource(R.layout.score_spinner_item);
        levelSpin.setAdapter(levelAdapter);
        modeSpin = findViewById(R.id.spinner);
        String[] modes = new String[] {"+ ×", "− ÷", "+ × − ÷"};
        modesAdapter = new ArrayAdapter<String>(this, R.layout.score_spinner_text, modes);
        modesAdapter.setDropDownViewResource(R.layout.score_spinner_item);
        modeSpin.setAdapter(modesAdapter);
        try{
            //onStart call highscore from database
            //setScoreBoards();
        }
        catch (Exception e){
            //If fail, do something user friendly
            //handleScoreError();
        }
    }

    public void setScoreListener(){

        score = findViewById(R.id.score_tabs);
        score.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LinearLayout top10 = findViewById(R.id.top_score);
                LinearLayout yourScore = findViewById(R.id.your_place);
                switch (tab.getPosition()) { //handle views based on selected tab
                    case 0:
                        if(currentScoreTab != 0) {
                            top10.setVisibility(View.GONE);
                            yourScore.setVisibility(View.VISIBLE);
                            top10.setAnimation( outToRightAnimation() );
                            yourScore.setAnimation( inFromLeftAnimation() );
                            currentScoreTab = 0;
                        }
                        break;
                    case 1:
                        if(currentScoreTab != 1) {
                            yourScore.setVisibility(View.GONE);
                            top10.setVisibility(View.VISIBLE);
                            yourScore.setAnimation( outToLeftAnimation() );
                            top10.setAnimation( inFromRightAnimation() );
                            currentScoreTab = 1;
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }

    //https://stackoverflow.com/questions/10009155/android-tabactivity-with-transition-animation
    public Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(240);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation outToRightAnimation()
    {
        Animation outToRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outToRight.setDuration(240);
        outToRight.setInterpolator(new AccelerateInterpolator());
        return outToRight;
    }

    public Animation outToLeftAnimation()
    {
        Animation outToLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outToLeft.setDuration(240);
        outToLeft.setInterpolator(new AccelerateInterpolator());
        return outToLeft;
    }

    public Animation inFromLeftAnimation()
    {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(240);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
}


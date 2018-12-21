package com.waki.ezpzmath;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ScoreActivity extends AppCompatActivity {

    TabLayout score;
    int currentScoreTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);


    }

    @Override
    protected void onStart() {
        super.onStart();
        setScoreListener();

    }

    public void setScoreListener(){

        score = findViewById(R.id.score_tabs);
        score.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LinearLayout top10 = findViewById(R.id.top_score);
                LinearLayout yourScore = findViewById(R.id.your_place);
                //CharSequence s = String.valueOf(tab.getPosition());
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                switch (tab.getPosition()) {
                    case 0:
                        if(currentScoreTab != 0) {
                            top10.setVisibility(View.GONE);
                            yourScore.setVisibility(View.VISIBLE);
                            top10.setAnimation( outToRightAnimation() );
                            yourScore.setAnimation( inFromLeftAnimation() );
                            currentScoreTab = 0;
                            Toast.makeText(getApplicationContext(), "in 0", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        if(currentScoreTab != 1) {
                            yourScore.setVisibility(View.GONE);
                            top10.setVisibility(View.VISIBLE);
                            yourScore.setAnimation( outToLeftAnimation() );
                            top10.setAnimation( inFromRightAnimation() );
                            currentScoreTab = 1;
                            Toast.makeText(getApplicationContext(), "in 1", Toast.LENGTH_LONG).show();
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
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(240);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    public Animation outToLeftAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(240);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    public Animation inFromLeftAnimation()
    {
        Animation intoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        intoLeft.setDuration(240);
        intoLeft.setInterpolator(new AccelerateInterpolator());
        return intoLeft;
    }


    //https://stackoverflow.com/questions/4605527/converting-pixels-to-dp
    public int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        double px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)Math.floor(px);
    }

    public void setMargins(View view, float left, float top, float right, float bottom){
        ViewGroup.MarginLayoutParams margins = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margins.setMargins(convertDpToPixel(left,this),
                convertDpToPixel(top,this),
                convertDpToPixel(right,this),
                convertDpToPixel(bottom,this));
    }
}


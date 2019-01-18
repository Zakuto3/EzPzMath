package com.waki.ezpzmath;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.provider.FontsContract;
import android.support.annotation.FontRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class HowToPlayActivity extends AppCompatActivity {
    ImageButton back_button;
    TextSwitcher textSwitcher;
    Button toRight;
    Button toLeft;
    String [] Pages = {"EzPzMath is a mathematical and educational application that improves your mental calculation skills, ranging from basic level to higher levels.",
                        "In the application you have three different modes (+ −), (× ÷) and (+ − × ÷). Each mode has three difficulty levels easy, medium and hard.",
                        "In the game, you will be given several empty boxes and different operations depending on the mode and the difficulty level you have chosen.", "At the bottom you will find some boxes with different numbers. Above the empty boxes, you will find two big bubbles. ",
                        "Your task is to use the numbers at the bottom to fill in the empty boxes and create an equation that gives a result equal to the answer in the above bubble.", "To get a score, you must finish five equations as fast as possible. The faster you end up, the higher your score will be.\nEnjoy your new experience!!"};
    int currentIndex = -1;
    int pageCount = Pages.length;
    boolean isPlaying;
    private boolean mIsBound = false;   //For anything about Music Service have a look on the comments in Main activity and MusicService class
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_how_to_play);

        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

        isPlaying = getIntent().getExtras().getBoolean("isPlaying");
        mServ = new MusicService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        if(isPlaying) {
            startService(music);
        }
        back_button = findViewById(R.id.backButton_howtoplay);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity(isPlaying);
            }
        });
        textSwitcher = findViewById(R.id.text_switcher);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView text1 = new TextView(HowToPlayActivity.this);
                text1.setGravity(Gravity.TOP | Gravity.LEFT);
                text1.setTextSize(20);
                text1.setMovementMethod(new ScrollingMovementMethod());
                text1.setTextColor(Color.parseColor("#266352"));
                return text1;
            }
        });
        Animation right =AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        Animation left = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);
        textSwitcher.setAnimation(right);
        textSwitcher.setAnimation(left);

        textSwitcher.setText("Use navigation buttons to navigate between the pages...");
        toRight= findViewById(R.id.right_button);
        toLeft = findViewById(R.id.left_button);

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
    @Override
    protected void onStart(){
        super.onStart();
        doBindService();
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        if(!mServ.isPlaying() && isPlaying){
            mServ.resumeMusic();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mServ.stopMusic();
        mServ.stopSelf();
        doUnbindService();
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(mIsBound) {
            doUnbindService();
        }
    }
    @Override
    public void onBackPressed() {
        if (true) {
            openSettingsActivity(isPlaying);
        } else {
            super.onBackPressed();
        }
    }
    public void openSettingsActivity( boolean isPlaying){
        Intent intent = new Intent(this,SettingsActivity.class);
        intent.putExtra("isPlaying", isPlaying);
        intent.putExtra("PreviousActivity", "Howtoplay");
        startActivity(intent);
    }
    void doBindService(){
        if(!mIsBound) {
            bindService(new Intent(this,MusicService.class),
                    Scon, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }

    }
    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }
}

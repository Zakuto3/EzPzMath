package com.waki.ezpzmath;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class LevelsActivity extends AppCompatActivity {

    private ImageButton Level_back_button;
    String[] operators;
    ImageButton soundBtn;
    private boolean mIsBound = false;       //For anything about Music Service have a look on the comments in Main activity and MusicService class
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;

        }
    };
    boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        soundBtn = (ImageButton)findViewById(R.id.imageButton11);

        isPlaying = getIntent().getExtras().getBoolean("isPlaying");
        setSoundIcon(isPlaying);
        mServ = new MusicService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        if(isPlaying) {
            startService(music);
        }
        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSound();
            }
        });


        operators = getIntent().getExtras().getStringArray("operators");
        Level_back_button = findViewById(R.id.imageButton9);
        Level_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModesActivity(isPlaying);
            }
        });

        Button EasyMode = findViewById(R.id.button1);
        EasyMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameActivity(1,isPlaying);
            }
        });

        Button NormalMode = findViewById(R.id.button2);
        NormalMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameActivity(2,isPlaying);
            }
        });

        Button HardMode = findViewById(R.id.button3);
        HardMode.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openGameActivity(3,isPlaying);
            }
        }));


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

    @Override
    public void onBackPressed() {
        if (true) {
            openModesActivity(isPlaying);
        } else {
            super.onBackPressed();
        }
    }

    public void openModesActivity(boolean isPlaying){
        Intent intent = new Intent(this, ModesActivity.class);
        intent.putExtra("isPlaying",isPlaying);
        startActivity(intent);
    }
    public void openGameActivity(int difficulty, boolean isPlaying)
    {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("operators", operators);
        intent.putExtra("isPlaying",isPlaying);
        startActivity(intent);
    }
    public void setSound(){
        if(mServ.isPlaying()) {
            mServ.pauseMusic();
            isPlaying = false;
            soundBtn.setImageResource(R.drawable.sound_off);
        }
        else{
            mServ.resumeMusic();
            isPlaying=true;
            soundBtn.setImageResource(R.drawable.sound_on);
        }
    }
    public void setSoundIcon(boolean isPlaying){
        if(isPlaying) {
            soundBtn.setImageResource(R.drawable.sound_on);
        }
        else{
            soundBtn.setImageResource(R.drawable.sound_off);
        }
    }
}

package com.waki.ezpzmath;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModesActivity extends AppCompatActivity {
    private Button first_mode_button;
    private Button second_mode_button;
    private Button third_mode_button;
    private ImageButton score_button;
    private ImageButton Settings_button;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    boolean isPlaying;
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
    @Override
    protected void onStop(){
        super.onStop();
        if(mIsBound) {
            doUnbindService();
        }
    }
    @Override
    protected void onStart(){
        doBindService();
        super.onStart();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        isPlaying = getIntent().getExtras().getBoolean("isPlaying");
        mServ = new MusicService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        if(isPlaying) {
            startService(music);
        }

        first_mode_button = findViewById(R.id.button1);
        first_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"+", "-"};
                openLevelsActivity(operators, isPlaying);
            }
        });

        second_mode_button = findViewById(R.id.button2);
        second_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"*", "/"};
                openLevelsActivity(operators, isPlaying);
            }
        });

        third_mode_button = findViewById(R.id.button3);
        third_mode_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                String[] operators = {"+", "-", "*", "/"};
                openLevelsActivity(operators,isPlaying);

            }
        });

        score_button = findViewById(R.id.imageButton10);
        score_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openScoreActivity(isPlaying);
            }
        });

        Settings_button = findViewById(R.id.imageButton9);
        Settings_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openSettingsActivity(isPlaying);
            }
        });
        if(user != null) {
            Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
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
    public void openLevelsActivity(String[] operators, boolean isPlaying){
        Intent intent = new Intent (this, LevelsActivity.class);
        intent.putExtra("operators", operators);
        intent.putExtra("isPlaying", isPlaying);
        startActivity(intent);
    }

    public void openScoreActivity(boolean isPlaying){
        Intent intent = new Intent (this, ScoreActivity.class);
        intent.putExtra("isPlaying",isPlaying);
        startActivity(intent);
    }

    public void openSettingsActivity(boolean isPlaying){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("isPlaying", isPlaying);
        startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
        alertbox.getWindow().setBackgroundDrawableResource(R.color.dialog_color);
        alertbox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#c5f5c2"));
        alertbox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#c5f5c2"));
    }
}

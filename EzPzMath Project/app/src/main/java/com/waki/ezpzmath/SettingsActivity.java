package com.waki.ezpzmath;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;


public class SettingsActivity extends AppCompatActivity {
    String [] Titles = {"How to play?", "Music", "Remove ADS", "Logout"};
    Integer [] Images ={R.drawable.howtoplay,R.drawable.settings_sound_on, R.drawable.remove_ads,R.drawable.logout};
    ListView settingsListView;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    public ImageButton back_button;
    private boolean mIsBound = false;       //For anything about Music Service have a look on the comments in Main activity class and MusicService class
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };
    private boolean [] itemToggled; //to track the sound button and be able to change the sound icon
    boolean isPlaying;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        isPlaying = getIntent().getExtras().getBoolean("isPlaying");
        mServ = new MusicService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        if(isPlaying) {
            startService(music);
        }

        manageAnim();

        itemToggled = new boolean[Images.length];
        Arrays.fill(itemToggled, false);
        if(isPlaying == false) {
            itemToggled[1] = true;
        }

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            Titles[3] = "Log in";
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() == null) {
                    //openLogInActivity();
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class );
                    startActivity(intent);
                }
            }
        };


        ListViewAdapter listViewAdapter = new ListViewAdapter(this, Titles,Images);
        settingsListView = findViewById(R.id.settings_listView);
        settingsListView.setAdapter(listViewAdapter);

        back_button = findViewById(R.id.imageButton_setting_back);
        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openModesActivity(isPlaying);
            }
        });
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    //How To Play
                    openHowToPlayActivity(isPlaying);
                }
                else if(position == 1){
                    //sound on/off
                    itemToggled[position] = !itemToggled[position];
                    setSound();
                    ImageView imageView = (ImageView) view.findViewById(R.id.settings_option_image);
                    imageView.setImageResource(itemToggled[position] ? R.drawable.settings_sound_of : R.drawable.settings_sound_on);
                }else if(position == 2){
                    //remove ADS
                    Toast.makeText(getApplicationContext(),"remove ADS",Toast.LENGTH_SHORT).show();

                }else if(position == 3){
                    mAuthListener = new FirebaseAuth.AuthStateListener() { //should consider this to be outside switch and under onAuthStateChanged
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            if(mAuth.getCurrentUser() == null) {
                                //openLogInActivity();
                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class );
                                startActivity(intent);

                            }
                        }
                    };
                    //Logout
                    mAuth.addAuthStateListener(mAuthListener);//per standard/industry this should be in onStart

                    mAuth.signOut();
                }
            }
        });
    }

    private void manageAnim() {
        String prevActivity = "";
        try{
            prevActivity = getIntent().getExtras().getString("PreviousActivity");
        }catch (Exception e){
            prevActivity = null;
        }
        if(prevActivity != null){
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        }else{
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }
    }

    public void setSoundIcon(boolean isPlaying){
        if(!isPlaying){
            Images[1] = R.drawable.settings_sound_of;
            //Arrays.fill(itemToggled,true);
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
    public void setSound(){
        if(mServ.isPlaying()) {
            mServ.pauseMusic();
            isPlaying = false;
        }
        else{
            mServ.resumeMusic();
            isPlaying = true;
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(mIsBound) {
            doUnbindService();
        }
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
    protected void onStart() {
        super.onStart();
        doBindService();
        setSoundIcon(isPlaying);
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
        Intent intent = new Intent (this, ModesActivity.class);
        intent.putExtra("isPlaying", isPlaying);
        intent.putExtra("PreviousActivity", "Settings");
        startActivity(intent);

    }
    public void openHowToPlayActivity(boolean isPlaying){
        Intent intent = new Intent(this,HowToPlayActivity.class);
        intent.putExtra("isPlaying",isPlaying);
        startActivity(intent);
    }
}
class ListViewAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] titles;
    private final Integer[] images;

    public ListViewAdapter(Activity context, String[] titles, Integer[] images){
        super(context,R.layout.list_view_settings,titles);
        this.context = context;
        this.titles = titles;
        this.images = images;

    }
    //getting a view for a specific option
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_view_settings, null,true);

        TextView titlesText = rowView.findViewById(R.id.settings_option_titel);
        ImageView iconImage = rowView.findViewById(R.id.settings_option_image);

        titlesText.setText(titles[position]);
        iconImage.setImageResource(images[position]);

        return rowView;
    }

}


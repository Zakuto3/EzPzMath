package com.waki.ezpzmath;

import android.app.Activity;
import android.content.Intent;
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


public class SettingsActivity extends AppCompatActivity {
    String [] Titles = {"How to play?", "Music", "Remove ADS", "Logout"};
    Integer [] Images ={R.drawable.howtoplay,R.drawable.settings_sound_on, R.drawable.remove_ads,R.drawable.logout};
    ListView settingsListView;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    public ImageButton back_button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
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
                openModesActivity();
            }
        });
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    //How To Play
                    openHowToPlayActivity();
                }
                else if(position == 1){
                    //sound on/off
                    Toast.makeText(getApplicationContext(),"sound on/off",Toast.LENGTH_SHORT).show();

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


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (true) {
            openModesActivity();
        } else {
            super.onBackPressed();
        }
    }

    public void openModesActivity(){
        Intent intent = new Intent (this, ModesActivity.class);
        startActivity(intent);

    }
    public void openHowToPlayActivity(){
        Intent intent = new Intent(this,HowToPlayActivity.class);
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


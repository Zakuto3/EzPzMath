package com.waki.ezpzmath;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class SettingsActivity extends AppCompatActivity {
    String [] Titles = {"How to play?", "Change password", "Music", "Remove ADS", "Logout"};
    Integer [] Images ={R.drawable.howtoplay,R.drawable.password,R.drawable.settings_sound_on, R.drawable.remove_ads,R.drawable.logout};
    ListView settingsListView;
    public ImageButton back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ListViewAdapter listViewAdapter = new ListViewAdapter(this, Titles,Images);
        settingsListView = (ListView) findViewById(R.id.settings_listView);
        settingsListView.setAdapter(listViewAdapter);

        back_button = (ImageButton) findViewById(R.id.imageButton_setting_back);
        back_button.setOnClickListener(new View.OnClickListener(){
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

        TextView titlesText = (TextView) rowView.findViewById(R.id.settings_option_titel);
        ImageView iconImage = (ImageView) rowView.findViewById(R.id.settings_option_image);

        titlesText.setText(titles[position]);
        iconImage.setImageResource(images[position]);

        return rowView;
    }
}


package com.waki.ezpzmath;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class ScoreActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TabLayout score;
    int currentScoreTab = 0;
    ArrayAdapter<String> modesAdapter;
    Spinner modeSpin;
    ArrayAdapter<String> levelAdapter;
    Spinner levelSpin;
    ProgressBar scoreLoader;
    TextView noScoreText;
    TextView noUserText;
    boolean youScoreIsLoaded = false;
    private ImageButton score_back_button;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score_back_button = findViewById(R.id.imageButton9);
        score_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModesActivity();
            }
        });
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


    @Override
    protected void onStart() {
        super.onStart();
        noScoreText = findViewById(R.id.no_score_text);
        noUserText = findViewById(R.id.no_user_text);
        scoreLoader = findViewById(R.id.score_loader);
        levelSpin = findViewById(R.id.spinner2);
        String[] levels = new String[] {"Easy", "Normal", "Hard"};
        levelAdapter = new ArrayAdapter<String>(this, R.layout.score_spinner_text, levels);
        levelAdapter.setDropDownViewResource(R.layout.score_spinner_item);
        levelSpin.setAdapter(levelAdapter);
        modeSpin = findViewById(R.id.spinner);
        String[] modes = new String[] {"+ −", "× ÷", "+ × − ÷"};
        modesAdapter = new ArrayAdapter<String>(this, R.layout.score_spinner_text, modes);
        modesAdapter.setDropDownViewResource(R.layout.score_spinner_item);
        modeSpin.setAdapter(modesAdapter);
        setScoreSpinListener();
        setScoreTabListener();
    }

    //handles the scoreboards when selecting spinners
    public void setScoreSpinListener(){
        modeSpin.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                scoreLoader.setVisibility(View.VISIBLE);
                setScoreboards();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        }));
        levelSpin.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                scoreLoader.setVisibility(View.VISIBLE);
                setScoreboards();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        }));
    }

    //handles the animation and visibility when pressing tabs
    public void setScoreTabListener(){
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
                            if(youScoreIsLoaded){
                                yourScore.setVisibility(View.VISIBLE);
                                yourScore.setAnimation( inFromLeftAnimation() );
                            }
                            else if(user == null){
                                noUserText.setVisibility(View.VISIBLE);
                                yourScore.setVisibility(View.GONE);
                                scoreLoader.setVisibility(View.GONE);
                                noScoreText.setVisibility(View.GONE);
                            }
                            else{
                                noUserText.setVisibility(View.GONE);
                                yourScore.setVisibility(View.GONE);
                                scoreLoader.setVisibility(View.GONE);
                                noScoreText.setVisibility(View.VISIBLE);
                            }
                            top10.setAnimation( outToRightAnimation() );
                            currentScoreTab = 0;
                        }
                        break;
                    case 1:
                        if(currentScoreTab != 1) {
                            noUserText.setVisibility(View.GONE);
                            noScoreText.setVisibility(View.GONE);
                            scoreLoader.setVisibility(View.GONE);
                            yourScore.setVisibility(View.GONE);
                            top10.setVisibility(View.VISIBLE);
                            if(youScoreIsLoaded){
                                yourScore.setAnimation( outToLeftAnimation() );
                            }
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

    //Set the scoreboards
    public void setScoreboards(){
        String mode = modeSpin.getSelectedItem().toString().toLowerCase().replaceAll("\\s", "");
        String level = levelSpin.getSelectedItem().toString().toLowerCase().replaceAll("\\s", "");
        final String scoreType = mode+"_"+level+"_score";
        db.collection("users")
                .orderBy(scoreType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(user != null)
                                setYourPosition(task.getResult(), scoreType);
                            else if(currentScoreTab == 0){
                                noScoreText.setVisibility(View.GONE);
                                noUserText.setVisibility(View.VISIBLE);
                            }
                            setTopTen(task.getResult(), scoreType);
                            scoreLoader.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String mail = document.getData().get("gmail").toString();
                                Log.d("setScoreboards", document.getId() + " => " + mail);
                            }
                        } else {
                            Log.w("setScoreboards", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    //sets the "YOU" tab
    public void setYourPosition(QuerySnapshot users, String scoreType){
        clearYourPos();
        boolean foundUser = false;
        LinearLayout scoreboard = findViewById(R.id.your_place);
        ArrayList<DocumentSnapshot> list = (ArrayList<DocumentSnapshot>) users.getDocuments();
        for(DocumentSnapshot item : list){
            String mail = item.get("gmail").toString();
            Log.d("setYourPosition", mail);
            if(mail.equals(user.getEmail())){
                int ind = list.indexOf(item);
                int viewid = 1;
                int placeNr;
                for(int i = ind-3; i<=ind+3;i++){
                    DocumentSnapshot us;
                    try{
                        us = list.get(i);
                    }catch (Exception e){
                        us = null;
                    }
                    if(us != null){
                        placeNr = (i+1);
                        TextView username = scoreboard.findViewWithTag("username"+viewid);
                        String name = us.get("displayname").toString();
                        username.setText(name);
                        TextView spot = scoreboard.findViewWithTag("your_place_spot"+viewid);
                        spot.setText(""+placeNr);
                        TextView score = scoreboard.findViewWithTag("usertime"+viewid);
                        score.setText(us.get(scoreType).toString());
                        viewid++;
                        Log.d("setYourPosition", name);
                    }
                }
                foundUser = true;
                break;
            }
        }
        if(foundUser){
            noScoreText.setVisibility(View.GONE);
            scoreboard.setVisibility(View.VISIBLE);
            youScoreIsLoaded = true;
        }
        else{
            scoreboard.setVisibility(View.GONE);
            noScoreText.setVisibility(View.VISIBLE);
            youScoreIsLoaded = false;
        }
    }

    //clears the "YOU" tab
    void clearYourPos(){
        LinearLayout scoreboard = findViewById(R.id.your_place);
        TextView view;
        for(int i = 1; i < 8; i++){
            view = scoreboard.findViewWithTag("username"+i);
            view.setText(R.string.no_entry_score);
            view = scoreboard.findViewWithTag("your_place_spot"+i);
            view.setText(R.string.no_entry_score);
            view = scoreboard.findViewWithTag("usertime"+i);
            view.setText(R.string.no_entry_score);
        }
    }

    //clears the top ten tab
    void clearTopTen(){
        LinearLayout top = findViewById(R.id.top_score);
        TextView view;
        for(int i = 1; i < 11; i++){
            view = top.findViewWithTag("topusername"+i);
            view.setText(R.string.no_entry_score);
            view = top.findViewWithTag("top_spot"+i);
            view.setText(R.string.no_entry_score);
            view = top.findViewWithTag("topusertime"+i);
            view.setText(R.string.no_entry_score);
        }
    }

    //sets the top ten tab
    public void setTopTen(QuerySnapshot users, String scoreType){
        clearTopTen(); //clear before adding new
        LinearLayout top = findViewById(R.id.top_score);
        int i = 1;
        for (QueryDocumentSnapshot data : users){
            if(i < 11) {
                Map<String, Object> user = data.getData();
                String name = user.get("displayname").toString();
                String score = user.get(scoreType).toString();
                Log.d("setTopTen", name);
                Log.d("setTopTen", score);
                Log.d("setTopTen", "topusername" + i);
                TextView username = (TextView) top.findViewWithTag("topusername" + i);
                username.setText(name);
                TextView spot = (TextView) top.findViewWithTag("top_spot" + i);
                spot.setText(i + "");
                TextView scoreview = (TextView) top.findViewWithTag("topusertime" + i);
                scoreview.setText(score);
                i++;
            }
            else break;
        }
        if(currentScoreTab == 1){
            noScoreText.setVisibility(View.GONE);
            noUserText.setVisibility(View.GONE);
        }
    }
}


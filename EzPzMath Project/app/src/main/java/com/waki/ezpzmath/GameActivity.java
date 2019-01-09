package com.waki.ezpzmath;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class GameActivity extends AppCompatActivity {
    int[] numbers;//will store all the numbers for the answer
    int[] operatorIndex;//will store which operator is for which position
    int position = 0, tempPos = 0;//keeps track of which answer box should be filled in next, if one other box is pressed
    boolean boxPressed = false;
    double result;
    Random rnd = new Random();
    public int seconds = 0, minutes = 0, hours = 0; //time so it can be accessed out of timer
    int difficulty = 3;
    String[] operators = { "+", "*", "-", "/" };//name says everything
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
    public int winCount=0;
    Timer t = new Timer();
    ImageButton backButton;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean isPlaying;
    ImageButton soundBtn;
    private boolean mIsBound = false;      //For anything about Music Service have a look on the comments in Main activity and MusicService class
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
        setContentView(R.layout.activity_game);
        difficulty = getIntent().getExtras().getInt("difficulty");
        operators = getIntent().getExtras().getStringArray("operators");
        soundBtn = findViewById(R.id.soundButton_game);
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
        int size = difficulty + 2;
        TextView count = findViewById(R.id.wincount);
        count.setText(winCount + "/5");
        test(size);//will use values sent in from other activities
        if (difficulty < 3)
        {
            while (!((result % 1) == 0))
            {
                test(size);
                Log.e("redo", "redone test");
            }
        }
        generateAnswerField(size);
        generateAnswerBoxes(1,size);
        manageDots();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                updateLabel.sendEmptyMessage(0);//this updates the timer field
            }
        };
        t.schedule(task,0,1000);
        backButton = findViewById(R.id.backButton_game);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModesActivity(isPlaying);
            }
        });
        markPosition();

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
    public void palySoundEffect(String effect){  //will manage the sound effects
        if(!isPlaying){
            return;
        }
        final MediaPlayer mPlayer1;
        final MediaPlayer mPlayer2;
        final MediaPlayer mPlayer3;
        final MediaPlayer mPlayer4;
        final MediaPlayer mPlayer5;

        switch (effect){
            case "click":
                mPlayer1 = MediaPlayer.create(this, R.raw.click);
                mPlayer1.setVolume(100, 100);
                mPlayer1.start();
                mPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });
                break;
            case "unclick":
                mPlayer2 = MediaPlayer.create(this, R.raw.unclick);
                mPlayer2.setVolume(100, 100);
                mPlayer2.start();
                mPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });
                break;
            case "wrong":
                mPlayer3 = MediaPlayer.create(this, R.raw.wrong_answer);
                mPlayer3.setVolume(100, 100);
                mPlayer3.start();
                mPlayer3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });
                break;
            case "correct":
                mPlayer4 = MediaPlayer.create(this, R.raw.correct_answer);
                mPlayer4.setVolume(100, 100);
                mPlayer4.start();
                mPlayer4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });
                break;
            case "win":
                mPlayer5 = MediaPlayer.create(this, R.raw.winning);
                mPlayer5.setVolume(100, 100);
                mPlayer5.start();
                mPlayer5.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });
                break;
            default:return;
        }
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
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler updateLabel = new Handler(){//updates timer and the variables which keeps track of time
        @Override
        public void handleMessage(Message msg) {
            seconds++;
            if (seconds == 60)
            {
                minutes++;
            }
            if (minutes == 60)
            {
                hours++;
            }
            seconds = seconds % 60;
            minutes = minutes % 60;
            TextView timer = findViewById(R.id.timer);
            timer.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
        }
    };





    private void generateAnswerField(int size)
    {
        final LinearLayout answers = findViewById(R.id.answerLayout);
        //ConstraintLayout layout = new ConstraintLayout(this);
        //ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //layout.setLayoutParams(params);
        //layout.setId(getUniqueId());
        //layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.single_past_answer));

        for (int i = 0; i < size; i++)
        {
            Button tempButton = new Button(this);
            tempButton.setText("");
            tempButton.setOnClickListener(getOnRemove(i));
            tempButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.empty_game_brick));
            answers.addView(tempButton);
            tempButton.getLayoutParams().height = 150;
            tempButton.getLayoutParams().width = 150;
            tempButton.setId(i);
            tempButton.setTag("nr_"+i);

            if (i < (size - 1))
            {
                TextView tempView = new TextView(this);
                if(operators[operatorIndex[i]].equals("/")) {
                    tempView.setText("÷");
                }
                else if(operators[operatorIndex[i]].equals("*")) {
                    tempView.setText("×");
                }
                else if (operators[operatorIndex[i]].equals("-")){
                    tempView.setText("−");
                }
                else {
                    tempView.setText(operators[operatorIndex[i]]);
                }
                tempView.setTextColor(Color.parseColor("#c5f5c2"));
                tempView.setTextSize(30);
                tempView.setTag("operator_"+i);
                tempView.setId(getUniqueId());
                //tempView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                answers.addView(tempView);
            }
        }
        //answers.addView(layout);
        //setConstraints(layout, answers, size);
    }

    private void markPosition()
    {
        Button btnPosition = findViewById(position);
        btnPosition.setBackgroundDrawable(getResources().getDrawable(R.drawable.current_game_brick));
    }

    View.OnClickListener getOnClick(final Button button)
    {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                palySoundEffect("click");
                Button temp = findViewById(position);
                button.setTextColor(button.getContext().getResources().getColor(R.color.unabeld_button));
                if (!temp.getText().equals(""))
                {
                    for (int i = 0; i < numbers.length+difficulty; i++)
                    {
                        Button check = findViewById(i+10);
                        if (check.getText().equals(temp.getText()))
                        {
                            if (!check.isEnabled())
                            {
                                check.setEnabled(true);
                                check.setTextColor(check.getContext().getResources().getColor(R.color.textcolor));
                                check.setBackgroundDrawable(getResources().getDrawable(R.drawable.game_brick));
                                break;
                            }
                        }
                    }
                }
                temp.setText(button.getText());
                temp.setTextColor(temp.getContext().getResources().getColor(R.color.textcolor));
                temp.setBackgroundDrawable(getResources().getDrawable(R.drawable.game_brick));
                button.setEnabled(false);
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.empty_game_brick));
                button.setTextColor(Color.parseColor("#789B8F"));
                if (position < numbers.length - 1 && !boxPressed)
                {
                    position++;
                }
                if (boxPressed)
                {
                    for (int i = 0; i < numbers.length; i++)
                    {
                        Button pos = findViewById(i);
                        if (pos.getText().equals(""))
                        {
                            position = i;
                             break;
                        }
                    }
                }
                markPosition();
            }
        };
    }

    View.OnClickListener getOnSubmit(final Context context)
    {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                StringBuilder equation = new StringBuilder();
                //LinearLayout pastAnswer = new LinearLayout(context);
                ConstraintLayout pastAnswer = new ConstraintLayout(context);
                //ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                pastAnswer.setId(getUniqueId());
                //params.setMargins(0,0,0,20);
                //pastAnswer.setLayoutParams(params);
                pastAnswer.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.single_past_answer));
                pastAnswer.setPadding(20,20,20,20);

                for (int i = 0; i < numbers.length; i++)
                {
                    Button temp = findViewById(i);
                    if (temp.getText().equals(""))
                    {
                        Toast toast = Toast.makeText(context, "All boxes need to be filled", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    if(i < numbers.length - 1)
                    {
                        equation.append(temp.getText()).append(operators[operatorIndex[i]]);
                    }
                    else
                    {
                        equation.append(temp.getText());
                    }
                    Button tempButton = new Button(context);
                    tempButton.setText(temp.getText());
                    tempButton.setTextColor(tempButton.getContext().getResources().getColor(R.color.textcolor));
                    tempButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.game_brick));
                    pastAnswer.addView(tempButton);
                    tempButton.getLayoutParams().height = 150;
                    tempButton.getLayoutParams().width = 150;
                    tempButton.setId(getUniqueId());
                    tempButton.setTag("nr_"+i);
                    if (i < numbers.length - 1)
                    {
                        TextView tempView = new TextView(context);
                        if(operators[operatorIndex[i]].equals("/")) {
                            tempView.setText("÷");
                        }
                        else if(operators[operatorIndex[i]].equals("*")) {
                            tempView.setText("×");
                        }
                        else if (operators[operatorIndex[i]].equals("-")){
                            tempView.setText("−");
                        }
                        else {
                            tempView.setText(operators[operatorIndex[i]]);
                        }
                        tempView.setTextColor(Color.parseColor("#c5f5c2"));
                        tempView.setTextSize(30);
                        tempView.setTag("operator_"+i);
                        tempView.setId(getUniqueId());
                        pastAnswer.addView(tempView);
                    }

                }
                Log.d("eq", equation.toString());
                try {
                    double resultAnswer = (double)engine.eval(equation.toString());
                    Button myResult = findViewById(R.id.result);

                    if (resultAnswer == result)
                    {
                        palySoundEffect("correct");
                        Log.d("win", "grats");
                        myResult.setText(String.format("%.2f", resultAnswer));
                        winCount++;
                        LinearLayout ll = findViewById(R.id.pastAnswers);
                        ll.removeAllViews();
                        Button pastResult = findViewById(R.id.result);
                        pastResult.setText("");
                        TextView count = findViewById(R.id.wincount);
                        count.setText( winCount + "/5");
                        manageDots();

                        if (winCount == 5)//how many wins it takes to win the game
                        {
                            //handle score when a game is won, extra 0 on hours for database sake
                            palySoundEffect("win");
                            saveScoreToDB(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                            showWin();

                        }
                        else
                        {
                            LinearLayout answerButtons = findViewById(R.id.answerLayout);
                            LinearLayout answerBoxes = findViewById(R.id.answerBoxLayout);
                            answerBoxes.removeAllViews();
                            answerButtons.removeAllViews();
                            int size = difficulty + 2;
                            test(size);
                            if (difficulty < 3)
                            {
                                while (!((result % 1) == 0))
                                {
                                    test(size);
                                    Log.e("redo", "redone test");
                                }
                            }
                            generateAnswerField(size);
                            generateAnswerBoxes(1 ,size);
                        }
                    }
                    else
                    {
                        palySoundEffect("wrong");
                        seconds += 20;
                        if (seconds > 60)
                        {
                            seconds = seconds % 60;
                            minutes++;
                        }
                        if (minutes > 60)
                        {
                            hours++;
                            minutes = 0;
                        }
                        LinearLayout past = findViewById(R.id.pastAnswers);
                        past.addView(pastAnswer);
                        setConstraints(pastAnswer, past, numbers.length);
                        myResult.setText(String.format("%.2f", resultAnswer));
                    }
                    for (int i = 0; i < numbers.length; i++)
                    {
                        Button temp = findViewById(i);
                        temp.setBackgroundDrawable(getResources().getDrawable(R.drawable.empty_game_brick));
                        temp.setText("");
                    }
                    for (int i = 0; i < numbers.length + 1; i++)
                    {
                        Log.e("error", ""+i);
                        Button temp = findViewById(i+10);
                        temp.setTextColor(temp.getContext().getResources().getColor(R.color.textcolor));
                        temp.setBackgroundDrawable(getResources().getDrawable(R.drawable.game_brick));
                        temp.setEnabled(true);
                    }
                    position = 0;
                } catch (ScriptException e) {
                    Log.e("error", e.getMessage());
                }
                markPosition();
            }
        };
    }

    int getUniqueId(){
        Random rand = new Random();
        int id;
        while (findViewById(id = rand.nextInt(Integer.MAX_VALUE) + 1) != null);
        return id;
    }

    void setConstraints(ConstraintLayout row, LinearLayout pastAnswersLayout, int length){
        ConstraintSet set = new ConstraintSet();
        TextView operator;
        Button number;
        set.clone(row);
        if(pastAnswersLayout != null){
            set.connect(row.getId(), ConstraintSet.LEFT, pastAnswersLayout.getId(), ConstraintSet.LEFT);
            set.connect(row.getId(), ConstraintSet.RIGHT, pastAnswersLayout.getId(), ConstraintSet.RIGHT);
        }
        for(int i = 0; i < length; i++){
            number = row.findViewWithTag("nr_"+i);
            if(i == 0){
                operator = row.findViewWithTag("operator_"+i);
                set.connect(operator.getId(), ConstraintSet.LEFT, number.getId(), ConstraintSet.RIGHT);
                set.connect(number.getId(), ConstraintSet.LEFT, row.getId(), ConstraintSet.LEFT);
                set.connect(number.getId(), ConstraintSet.RIGHT, operator.getId(), ConstraintSet.LEFT);
                //Log.d("setConstraints", "IF - "+number.getId()+ " operator = "+operator.getId());
            }
            else if(i == (length-1)){
                operator = row.findViewWithTag("operator_"+(i-1));
                set.connect(operator.getId(), ConstraintSet.RIGHT, number.getId(), ConstraintSet.LEFT);
                set.connect(number.getId(), ConstraintSet.LEFT, operator.getId(), ConstraintSet.RIGHT);
                set.connect(number.getId(), ConstraintSet.RIGHT, row.getId(), ConstraintSet.RIGHT);
                //Log.d("setConstraints", "ELSE IF - "+number.getId()+ " operator = "+operator.getId());
            }
            else{
                operator = row.findViewWithTag("operator_"+i);
                set.connect(operator.getId(), ConstraintSet.LEFT, number.getId(), ConstraintSet.RIGHT);
                set.connect(number.getId(), ConstraintSet.RIGHT, operator.getId(), ConstraintSet.LEFT);
                operator = row.findViewWithTag("operator_"+(i-1));
                set.connect(operator.getId(), ConstraintSet.RIGHT, number.getId(), ConstraintSet.LEFT);
                set.connect(number.getId(), ConstraintSet.LEFT, operator.getId(), ConstraintSet.RIGHT);
                //Log.d("setConstraints", "ELSE - "+number.getId()+ " operator = "+operator.getId());
            }
            set.connect(operator.getId(), ConstraintSet.TOP, row.getId(), ConstraintSet.TOP);
            set.connect(operator.getId(), ConstraintSet.BOTTOM, row.getId(), ConstraintSet.BOTTOM);
        }
        set.applyTo(row);
    }

    private void showWin()//endgame screen
    {
        //show the custom dialog that have been designed to matxh with the prototype...
        CustomDialogClass cdd = new CustomDialogClass(this, seconds, minutes, hours, operators, difficulty, isPlaying);
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        t.cancel();
        cdd.show();
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("WinScreen");
        t.cancel();
        builder.setMessage("You won on: " + String.format("%d:%02d:%02d", hours, minutes, seconds));
        builder.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                winCount = 0;
                minutes = 0;
                seconds = 0;
                hours = 0;
                LinearLayout answerButtons = findViewById(R.id.answerLayout);
                LinearLayout answerBoxes = findViewById(R.id.answerBoxLayout);
                answerBoxes.removeAllViews();
                answerButtons.removeAllViews();
                int size = difficulty + 2;
                test(size);//will use values sent in from other activities
                generateAnswerField(size);
                generateAnswerBoxes(1 ,size);
                t = new Timer();
                TimerTask task = new TimerTask(){
                    @Override
                    public void run() {
                        updateLabel.sendEmptyMessage(0);//this updates the timer field
                    }
                };
                t.schedule(task,0,1000);
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openModesActivity();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();*/
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey()//if key back is pressed show message
    {

        final AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the current session?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        openModesActivity(isPlaying);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
        alertbox.getWindow().setBackgroundDrawableResource(R.color.dialog_color);
        alertbox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#c5f5c2"));
        alertbox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#c5f5c2"));

    }

    public void openModesActivity(boolean isPlaying){
        Intent intent = new Intent(this, ModesActivity.class);
        intent.putExtra("isPlaying",isPlaying);
        startActivity(intent);
    }

    View.OnClickListener getOnRemove(final int pos) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempPos = position;
                Button temp = findViewById(pos);
                for (int i = 0; i < numbers.length + difficulty; i++) {
                    Button check = findViewById(i + 10);
                    if (check != null) {
                        if (check.getText().equals(temp.getText())) {
                            if (!check.isEnabled()) {
                                check.setEnabled(true);
                                check.setTextColor(check.getContext().getResources().getColor(R.color.textcolor));
                                check.setBackgroundDrawable(getResources().getDrawable(R.drawable.game_brick));
                                break;
                            }
                        }
                    }

                }
                temp.setText("");
                Button prev = findViewById(position);
                prev.setBackgroundDrawable(getResources().getDrawable(R.drawable.game_brick));
                position = pos;
                boxPressed = true;
                markPosition();
            }
        };
    }

    @SuppressLint("ResourceAsColor")
    private void generateAnswerBoxes(int difficulty, int size)
    {//this generates the lowest boxes
        final LinearLayout boxes = findViewById(R.id.answerBoxLayout);
        int[] temp = new int[difficulty + size];
        for (int i = 0; i < size; i++)
        {
            temp[i] = numbers[i];
        }

        for (int i = size; i < size + difficulty; i++) {
            if(difficulty == 1)
            {
                temp[i] = rnd.nextInt((5-1) + 1) + 1;
            }
            else if (difficulty == 2)
            {
                temp[i] = rnd.nextInt((7-1) + 1) + 1;
            }
            else
            {
                temp[i] = rnd.nextInt((9-1) + 1) + 1;
            }
        }
        shuffleArr(temp);
        for (int i = 0; i < size + difficulty; i++)
        {

            Button tempButton = new Button(this);
            tempButton.setText(Integer.toString(temp[i]));
            tempButton.setTextColor(tempButton.getContext().getResources().getColor(R.color.textcolor));
            //tempButton.setBackgroundColor(tempButton.getContext().getResources().getColor(R.color.res_answ_text_color));
            tempButton.setGravity(Gravity.CENTER);
            tempButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.game_brick));
            tempButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);


            tempButton.setOnClickListener(getOnClick(tempButton));
            tempButton.setId(i+10);
            boxes.addView(tempButton);
            tempButton.getLayoutParams().height = 140;
            tempButton.getLayoutParams().width = 140;
            if (i < size - 1)
            {
                TextView tempView = new TextView(this);//temp solution
                tempView.setText(" ");
                boxes.addView(tempView);
            }
        }
        Button submit = findViewById(R.id.submit_button);
        submit.setOnClickListener(getOnSubmit(this));
        //////////////////////////////////////////////////////////
        /*Button submit = new Button(this);
        submit.setText("✓");
        submit.setBackgroundColor(Color.parseColor("#55cb4d"));
        submit.setTextSize(20);
        submit.setGravity(Gravity.CENTER);
        submit.setOnClickListener(getOnSubmit(this));
        boxes.addView(submit);
        submit.getLayoutParams().height = 160;
        submit.getLayoutParams().width = 150;*/


    }

    private void shuffleArr(int[] ar)//for shuffling the answer boxes
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            Log.d("first", ""+ar[i]);
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
            Log.d("second", ""+ar[i]);
        }
    }

    private void test(int size)//the function which creates the equation
    {
        numbers = new int[size];
        operatorIndex = new int[size - 1];
        result = 0;

        StringBuilder equation = new StringBuilder();
        for (int i = 0; i < size; i++)
        {
            if(difficulty == 1)
            {
                numbers[i] = rnd.nextInt((5-1) + 1) + 1;
            }
            else if (difficulty == 2)
            {
                numbers[i] = rnd.nextInt((7-1) + 1) + 1;
            }
            else
            {
                numbers[i] = rnd.nextInt((9-1) + 1) + 1;
            }

        }
        int divides = 0;
        for (int i = 0; i < size - 1; i++)
        {
            operatorIndex[i] = rnd.nextInt((operators.length));
            if (operators[operatorIndex[i]].equals("/"))
            {
                if (divides >= 2)
                {
                    operatorIndex[i] = rnd.nextInt((operators.length - 1));
                    Log.d("change", "Changed operator");
                }
                divides++;
            }
            Log.d("msg", ""+operators[operatorIndex[i]]);
        }
        for (int i = 0; i < size; i++)
        {
            if(i < size - 1)
            {
                equation.append(numbers[i]).append(operators[operatorIndex[i]]);
            }
            else
            {
                equation.append(numbers[i]);
            }
        }
        Button myResult = findViewById(R.id.answer);
        //DecimalFormat df = new DecimalFormat("#,##");
        try {
            result = (double)engine.eval(equation.toString());
            Log.d("msg", ""+result);
            //df.setRoundingMode(RoundingMode.CEILING);//dosen't write out the correct number if it has decimals
            myResult.setText(String.format("%.2f",result));
        } catch (ScriptException e) {
            Log.e("error", e.getMessage());
            myResult.setText(e.getMessage());
        }
        Log.d("answer", equation.toString());
    }

    //run while you can
    private void saveScoreToDB(final String score){
        if(user!= null){
            final String scoreString = buildScorestring();
            DocumentReference checkScore = db.collection("users").document(user.getEmail());
            checkScore.get().addOnCompleteListener((new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        String currHigh;
                        if(task.getResult().get(scoreString) != null){ //check if user already have a score
                            currHigh = task.getResult().get(scoreString).toString();
                            int compare = score.compareToIgnoreCase(currHigh);
                            Log.d("saveScore", String.valueOf(compare));
                            if(compare < 0){   //true if it is new highscore, scary but it works
                                Map<String, Object> userscore = new HashMap<String, Object>();
                                userscore.put(scoreString, score);
                                Log.d("saveScore", "New highscore");
                                db.collection("users").document(user.getEmail()).set(userscore, SetOptions.merge());
                            }
                            else{ // else its not a new highscore, dont replace in DB
                                Log.d("saveScore", "Not new highscore");
                            }
                        }
                        else{ //user dont have a score, add the one user got
                            Map<String, Object> userscore = new HashMap<String, Object>();
                            userscore.put(scoreString, score);
                            db.collection("users").document(user.getEmail()).set(userscore, SetOptions.merge());
                            Log.d("saveScore", "Score did not exist, add");
                        }
                    }
                    else{
                        Log.d("saveScore", task.getException().getMessage());
                    }
                }
            }));
            Log.d("SCORE", score);

        }
    }

    //builds a string needed for database call,
    // properties in DB have special string to identify mode/level
    private String buildScorestring(){
        String scoreString;
        String ops = "";
        for (int i = 0; i < operators.length; i++){
            ops = ops.concat(operators[i]);
        }
        if(ops.contains("+") && ops.contains("-") && !ops.contains("/")){
            scoreString = "+−_";
        }
        else if(ops.contains("*") && ops.contains("/") && !ops.contains("+")){
            scoreString = "×÷_";
        }
        else{
            scoreString = "+×−÷_";
        }
        switch (difficulty){
            case 1:
                scoreString = scoreString.concat("easy_score");
                break;
            case 2 :
                scoreString = scoreString.concat("normal_score");
                break;
            case 3:
                scoreString = scoreString.concat("hard_score");
                break;
        }
        Log.d("SCORESTRING", scoreString);
        return scoreString;
    }

    private void manageDots(){
        ConstraintLayout dots = findViewById(R.id.stage_dots);
        int bigdot = (int)convertDpToPixel(12, this);
        int smalldot = (int)convertDpToPixel(10, this);
        ImageView dot;
        for(int i = 1; i <= 5; i++){
            dot = dots.findViewWithTag("stage_"+i);
            if(dot != null){
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) dot.getLayoutParams();
                if(i <= winCount){
                    dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.completed_dot));
                    params.width = smalldot;
                    params.height = smalldot;
                    params.setMargins(0,0,0,0);
                }
                else if(i == (winCount+1)){
                    dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.current_dot));
                    params.width = bigdot;
                    params.height = bigdot;
                    params.setMargins(0,0,0,(int)convertDpToPixel(2, this));
                }
                else {
                    dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.empty_dot));
                    params.width = smalldot;
                    params.height = smalldot;
                    params.setMargins(0,0,0,0);
                }
                dot.setLayoutParams(params);

            }
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    //https://stackoverflow.com/questions/4605527/converting-pixels-to-dp
    public static float convertDpToPixel(float dp, Context context){
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}


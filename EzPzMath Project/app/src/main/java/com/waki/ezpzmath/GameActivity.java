package com.waki.ezpzmath;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    int seconds = 0, minutes = 0, hours = 0; //time so it can be accessed out of timer
    int difficulty = 3;
    String[] operators = { "+", "*", "-", "/" };//name says everything
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
    int winCount=0;
    Timer t = new Timer();
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        difficulty = getIntent().getExtras().getInt("difficulty");
        operators = getIntent().getExtras().getStringArray("operators");
        int size = difficulty + 2;
        TextView count = findViewById(R.id.wincount);
        count.setText(winCount + "/5");
        test(size);//will use values sent in from other activities
        if (difficulty <= 2)
        {
            while (!(result % 1 == 0))
            {
                test(size);
            }
        }
        generateAnswerField(size);
        generateAnswerBoxes(1,size);
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
                openModesActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (true) {
            openModesActivity();
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
            TextView timer = (TextView)findViewById(R.id.timer);
            timer.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
        }
    };

    public void onDestroy() {
        super.onDestroy();
    }



    private void generateAnswerField(int size)
    {
        final LinearLayout answers = (LinearLayout)findViewById(R.id.answerLayout);

        for (int i = 0; i < size; i++)
        {
            Button tempButton = new Button(this);
            tempButton.setText("");
            tempButton.setId(i);
            tempButton.setOnClickListener(getOnRemove(i));
            answers.addView(tempButton);
            tempButton.getLayoutParams().height = 150;
            tempButton.getLayoutParams().width = 150;
            if (i < size - 1)
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
                tempView.setTextSize(30);
                tempView.setTextColor(Color.parseColor("#c5f5c2"));
                tempView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                answers.addView(tempView);
            }
        }
    }

    View.OnClickListener getOnClick(final Button button)
    {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Button temp = (Button)findViewById(position);
                button.setTextColor(button.getContext().getResources().getColor(R.color.unabeld_button));
                if (!temp.getText().equals(""))
                {

                    for (int i = 0; i < numbers.length+difficulty; i++)
                    {
                        Button check = (Button)findViewById(i+10);
                        if (check.getText().equals(temp.getText()))
                        {
                            if (!check.isEnabled())
                            {
                                check.setEnabled(true);
                                check.setTextColor(check.getContext().getResources().getColor(R.color.textcolor));
                                break;
                            }
                        }
                    }
                }
                temp.setText(button.getText());
                temp.setTextColor(temp.getContext().getResources().getColor(R.color.textcolor));
                button.setEnabled(false);
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
                    boxPressed = false;
                }
            }
        };
    }

    View.OnClickListener getOnSubmit(final Context context)
    {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                StringBuilder equation = new StringBuilder();
                LinearLayout pastAnswer = new LinearLayout(context);


                for (int i = 0; i < numbers.length; i++)
                {
                    Button temp = (Button)findViewById(i);
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
                    pastAnswer.addView(tempButton);
                    tempButton.getLayoutParams().height = 150;
                    tempButton.getLayoutParams().width = 150;
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
                        pastAnswer.addView(tempView);
                    }

                }
                Log.d("eq", equation.toString());
                try {
                    double resultAnswer = (double)engine.eval(equation.toString());
                    Button myResult = (Button)findViewById(R.id.result);

                    if (resultAnswer == result)
                    {
                        Log.d("win", "grats");//todo make things happen when won
                        myResult.setText(String.format("%.2f", resultAnswer));
                        winCount++;
                        LinearLayout ll = (LinearLayout)findViewById(R.id.pastAnswers);
                        ll.removeAllViews();
                        Button pastResult = findViewById(R.id.result);
                        pastResult.setText("");
                        TextView count = findViewById(R.id.wincount);
                        count.setText( winCount + "/5");
                        if (winCount == 5)//how many wins it takes to win the game
                        {
                            showWin();
                        }
                        else
                        {
                            LinearLayout answerButtons = findViewById(R.id.answerLayout);
                            LinearLayout answerBoxes = findViewById(R.id.answerBoxLayout);
                            answerBoxes.removeAllViews();
                            answerButtons.removeAllViews();
                            int size = difficulty + 2;
                            test(size);//will use values sent in from other activities
                            generateAnswerField(size);
                            generateAnswerBoxes(1 ,size);
                        }
                    }
                    else
                    {
                        minutes++;
                        LinearLayout past = (LinearLayout)findViewById(R.id.pastAnswers);
                        past.addView(pastAnswer);
                        myResult.setText(String.format("%.2f", resultAnswer));
                    }
                    for (int i = 0; i < numbers.length; i++)
                    {
                        Button temp = (Button)findViewById(i);
                        temp.setText("");
                    }
                    for (int i = 0; i < numbers.length + 1; i++)
                    {
                        Log.e("error", ""+i);
                        Button temp = (Button)findViewById(i+10);
                        temp.setTextColor(temp.getContext().getResources().getColor(R.color.textcolor));
                        temp.setEnabled(true);
                    }
                    position = 0;
                } catch (ScriptException e) {
                    Log.e("error", e.getMessage());
                }
            }
        };
    }

    private void showWin()//endgame screen
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        dialog.show();
    }

    public void openModesActivity(){
        Intent intent = new Intent(this, ModesActivity.class);
        startActivity(intent);
    }

    View.OnClickListener getOnRemove(final int pos)
    {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!(pos == position))
                {
                    tempPos = position;
                    Button temp = (Button)findViewById(pos);
                    for (int i = 0; i < numbers.length+difficulty; i++)
                    {
                        Button check = (Button)findViewById(i+10);
                        if (check.getText().equals(temp.getText()))
                        {
                            if (!check.isEnabled())
                            {
                                check.setEnabled(true);
                                check.setTextColor(check.getContext().getResources().getColor(R.color.textcolor));
                                break;
                            }
                        }
                    }
                    temp.setText("");
                    position = pos;
                    boxPressed = true;
                }
            }
        };
    }

    @SuppressLint("ResourceAsColor")
    private void generateAnswerBoxes(int difficulty, int size)//do no know what this variable will become
    {//this generates the lowest boxes
        final LinearLayout boxes = (LinearLayout)findViewById(R.id.answerBoxLayout);
        int[] temp = new int[difficulty + size];
        for (int i = 0; i < size; i++)
        {
            temp[i] = numbers[i];
        }

        for (int i = size; i < size + difficulty; i++) {
            temp[i] = rnd.nextInt((9-1) + 1) + 1;
        }
        shuffleArr(temp);
        for (int i = 0; i < size + difficulty; i++)
        {

            Button tempButton = new Button(this);
            tempButton.setText(Integer.toString(temp[i]));
            tempButton.setTextColor(tempButton.getContext().getResources().getColor(R.color.textcolor));
            //tempButton.setBackgroundColor(tempButton.getContext().getResources().getColor(R.color.res_answ_text_color));
            tempButton.setGravity(Gravity.CENTER);


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
        /*Button submit = new Button(this);
        submit.setText("✓");
        submit.setBackgroundColor(Color.parseColor("#55cb4d"));
        submit.setTextSize(20);
        submit.setGravity(Gravity.CENTER);
        submit.setOnClickListener(getOnSubmit(this));
        boxes.addView(submit);
        submit.getLayoutParams().height = 160;
        submit.getLayoutParams().width = 150;*/
        Button submit = findViewById(R.id.submit_button);
        submit.setOnClickListener(getOnSubmit(this));

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
            numbers[i] = rnd.nextInt((9-1) + 1) + 1;
        }
        for (int i = 0; i < size - 1; i++)
        {
            operatorIndex[i] = rnd.nextInt((operators.length));
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
        Button myResult = (Button) findViewById(R.id.answer);
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
}


package com.waki.ezpzmath;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    int difficulty = 3;//todo if diff is sent in as string make function which decides how many extra boxes
    String[] operators = { "+", "×", "-", "÷" };//name says everything
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        test(5);//will use values sent in from other activities
        generateAnswerField(5);
        generateAnswerBoxes(difficulty,5);
        Timer t = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                updateLabel.sendEmptyMessage(0);//this updates the timer field
            }
        };
        t.schedule(task,0,1000);
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



    private void generateAnswerField(int size)//todo make boxes better looking and make so it has has some space between each other
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
                tempView.setText(operators[operatorIndex[i]]);
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
                temp.setText(button.getText());
                button.setEnabled(false);
                if (position < numbers.length - 1 && !boxPressed)
                {
                    position++;
                }
                if (boxPressed)
                {
                    position = tempPos;
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
                    pastAnswer.addView(tempButton);
                    tempButton.getLayoutParams().height = 150;
                    tempButton.getLayoutParams().width = 150;
                    if (i < numbers.length - 1)
                    {
                        TextView tempView = new TextView(context);
                        tempView.setText(operators[operatorIndex[i]]);
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
                    }
                    else
                    {
                        LinearLayout past = (LinearLayout)findViewById(R.id.pastAnswers);
                        past.addView(pastAnswer);
                        for (int i = 0; i < numbers.length; i++)
                        {
                            Button temp = (Button)findViewById(i);
                            temp.setText("");
                        }
                        for (int i = 0; i < numbers.length + difficulty; i++)
                        {
                            Button temp = (Button)findViewById(i+10);
                            temp.setEnabled(true);
                        }
                        position = 0;
                        myResult.setText(String.format("%.2f", resultAnswer));
                        minutes++;
                    }
                } catch (ScriptException e) {
                    Log.e("error", e.getMessage());
                }
            }
        };
    }

    View.OnClickListener getOnRemove(final int pos)
    {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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
                            break;
                        }
                    }
                }
                temp.setText("");
                position = pos;
                boxPressed = true;
            }
        };
    }

    private void generateAnswerBoxes(int difficulty, int size)//do no know what this variable will become
    {
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
            tempButton.setOnClickListener(getOnClick(tempButton));
            tempButton.setId(i+10);
            boxes.addView(tempButton);
            tempButton.getLayoutParams().height = 110;
            tempButton.getLayoutParams().width = 110;
            if (i < size - 1)
            {
                TextView tempView = new TextView(this);//temp solution
                tempView.setText(" ");
                boxes.addView(tempView);
            }
        }
        Button submit = new Button(this);
        submit.setText("Submit");
        submit.setOnClickListener(getOnSubmit(this));
        boxes.addView(submit);
        submit.getLayoutParams().height = 150;
        submit.getLayoutParams().width = 150;
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

    private void test(int size)
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
            operatorIndex[i] = rnd.nextInt((3) + 1);
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


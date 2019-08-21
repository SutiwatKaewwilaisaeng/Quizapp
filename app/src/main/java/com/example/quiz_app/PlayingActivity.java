package com.example.quiz_app;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quiz_app.Common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener
{
    final  static long INTERVAL = 1000;
    final  static long TIMEOUT = 7000;

    int progressValue = 0;

    CountDownTimer mCountDown;
    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;
    //Firebase
    //FirebaseDatabase database;
    //DatabaseReference questions;

    ProgressBar progressBar;
    ImageView question_image;
    Button btnA,btnB,btnC,btnD;
    TextView txtScore,txtQuestionNum,question_text;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);



        txtScore = findViewById(R.id.txtScore);
        txtQuestionNum = findViewById(R.id.txtTotalQuestion);
        question_text = findViewById(R.id.question_text);
        question_image = findViewById(R.id.question_image);
        progressBar = findViewById(R.id.Progressbar);

        btnA = findViewById(R.id.btnAnswerA);
        btnB = findViewById(R.id.btnAnswerB);
        btnC = findViewById(R.id.btnAnswerC);
        btnD = findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        mCountDown.cancel();
        if (index < totalQuestion) // still have question in list
        {
            Button clickButton = (Button)view;
            if (clickButton.getText().equals(Common.questionList.get(index).getCorrectAnswer()))
            {
                //Choose correct answer
                score+=10;
                correctAnswer++;
                showQuestion(++index); // Next Question


            }
            else
            {
                //Choose wrong answer
                Intent intent = new Intent(this,DoneActivity.class);
                Bundle dataSend = new Bundle();
                dataSend.putInt("SCORE",score);
                dataSend.putInt("TOTAL",totalQuestion);
                dataSend.putInt("CORRECT",correctAnswer);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();
            }
            txtScore.setText(String.format("%d",score));
        }
    }

    private void showQuestion(int index)
    {
        if (index < totalQuestion)
        {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d",thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;
            if (Common.questionList.get(index).getIsImageQuestion().equals("true"))
            {
                //If is Image
                Picasso.with(getBaseContext())
                        .load(Common.questionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);

            }
            else
            {
                question_text.setText(Common.questionList.get(index).getQuestion());

                //If question is text , we  will set image to invisible
                question_image.setVisibility(View.INVISIBLE);
                question_text.setVisibility(View.VISIBLE);
            }

            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());

            mCountDown.start(); //Start timer
        }
        else
        {
            //if it is final question
            Intent intent = new Intent(this,DoneActivity.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE",score);
            dataSend.putInt("TOTAL",totalQuestion);
            dataSend.putInt("CORRECT",correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    //Press Ctrl+O

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.questionList.size();
        mCountDown = new CountDownTimer(TIMEOUT,INTERVAL) {
            @Override
            public void onTick(long minisec)
            {
                progressBar.setProgress(progressValue);
                progressValue++;

            }

            @Override
            public void onFinish()
            {
                mCountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
}

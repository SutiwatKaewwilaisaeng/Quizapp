package com.example.quiz_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quiz_app.Common.Common;
import com.example.quiz_app.Model.QuestionScore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoneActivity extends AppCompatActivity
{
        Button btnTryAgain;
        TextView txtResultScore,getTxtResultQuestion;
        ProgressBar progressBar;

        FirebaseDatabase database;
        DatabaseReference question_score;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");

        txtResultScore = findViewById(R.id.txtTotalscore);
        getTxtResultQuestion = findViewById(R.id.txtTotalQuestion);
        progressBar = findViewById(R.id.doneProgressbar);
        btnTryAgain = findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoneActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Get data from dundle and set to view
        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            int score = extra.getInt("SCORE");
            int totalQuestion = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRECT") ;

            txtResultScore.setText(String.format("SCORE : %d",score));
            getTxtResultQuestion.setText(String.format("PASSED : %d / %d",correctAnswer,totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);

            //Upload point to DB
            question_score.child(String.format("%s_%s", Common.currentUser1.getUserName(),
                                                                Common.categoryid))
                    .setValue(new QuestionScore(String.format("%s_%s",Common.currentUser1.getUserName(),
                            Common.categoryid),
                            Common.currentUser1.getUserName(),
                            String.valueOf(score),
                            Common.categoryid,
                            Common.categoryName));


        }

    }
}

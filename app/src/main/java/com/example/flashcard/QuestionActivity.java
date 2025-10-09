package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class QuestionActivity extends BaseActivity {

    ImageView imageQuestion;
    List<String> answersList;
    Toast toast;
    Spinner answersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        linkButton(R.id.HomeQuestionImageView, MainActivity.class, false);

        //Create all the intent
        Intent srcIntent = getIntent();
        Intent intent = new Intent(this, ListQuestionActivity.class);

        //Take the variable named question
        Question question =srcIntent.getParcelableExtra("Question");

        LinearLayout answersLayout = findViewById(R.id.QuestionLineaLayout);

        imageQuestion = findViewById(R.id.PeopleImageView2);
        answersList = question.answers;

        //If there's no image, put nothing
        Log.d("QuestionActivity", ""+question.id);
        if (question.id != 0) {
            imageQuestion.setImageResource(question.id);
        } else {
            imageQuestion.setImageResource(R.drawable.not_found);
        }

        for(int i = 0; i < answersList.size(); i++){
            final int j = i;

            // Create a button with the response and add it to the view
            Button answerButton = new Button(this);
            answerButton.setText(question.answers.get(i));
            answersLayout.addView(answerButton);

            //Take the button to say it's the good or the wrong
            answerButton.setOnClickListener( view ->{

                //Add a different toast if it's the good response or the wrong
                if(j != question.correct){
                    toast = Toast.makeText(this, "Mauvaise réponse, la bonne réponse " +
                            "était : "+question.answers.get(question.correct)+".", Toast.LENGTH_LONG);
                }
                else{
                    toast = Toast.makeText(this, "Bonne réponse !", Toast.LENGTH_LONG);
                }

                toast.show();
                startActivity(intent);
                finish();
            });
        }
    }
}
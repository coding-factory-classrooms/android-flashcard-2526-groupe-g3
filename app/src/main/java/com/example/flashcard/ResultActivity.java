package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class ResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ImageView shareImageView = findViewById(R.id.ShareImageView);


        Intent intent = getIntent();
        int correctAnswerCount = intent.getIntExtra("correctAnswerCount", 0);
        int totalQuestions = intent.getIntExtra("totalQuestions", 0);
        int difficulty = intent.getIntExtra("difficulty", 0);
        String difficultyName = "";
        switch (difficulty){
            case 0:
                difficultyName = "SIMPLE";
                break;
            case 1:
                difficultyName = "MOYEN";
                break;
            case 2:
                difficultyName = "DIFFICILE";
                break;
            case 3:
                difficultyName = "HARDCORE";
                break;
        }

        //make string of % correct
        String correctResultPercent = String.format(Locale.US, "%.2f", (float) correctAnswerCount * 100 / (float) totalQuestions) + " %";
        TextView resultPercentText = findViewById(R.id.ResultPercentTextView);
        resultPercentText.setText(correctResultPercent);

        //make string of correct / total questions
        String correctResultNumber = correctAnswerCount + " / " + totalQuestions;
        TextView resultNumberText = findViewById(R.id.ResultNumberTextView);
        resultNumberText.setText(correctResultNumber);

        linkButton(R.id.HomeResultImageView, MainActivity.class);
        linkButton(R.id.BackButton, DifficultyActivity.class);

        String shareText = "YOOO JAI FAIT LE TEST SOSIE EN " + difficultyName + " ET JAI EU " + correctResultNumber;

        shareImageView.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareSub = "I LOVE SOSIE";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        });

        findViewById(R.id.QuestionButton2).setOnClickListener(view ->{
            ArrayList<Question> questions= intent.getParcelableArrayListExtra("failedQuestion");
            onButtonShowPopupWindowClick(view,questions , null, difficulty);
        });

    }
}
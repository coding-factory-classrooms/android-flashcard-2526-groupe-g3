package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        Intent intent = getIntent();
        int correctAnswerCount = intent.getIntExtra("correctAnswerCount", 0);
        int totalQuestions = intent.getIntExtra("totalQuestions", 0);

        String correctResultPercent = String.format(Locale.US, "%.2f", (float) correctAnswerCount * 100 / (float) totalQuestions) + " %";
        TextView resultPercentText = findViewById(R.id.ResultPercentTextView);
        resultPercentText.setText(correctResultPercent);

        String correctResultNumber = correctAnswerCount + " / " + totalQuestions;
        TextView resultNumberText = findViewById(R.id.ResultNumberTextView);
        resultNumberText.setText(correctResultNumber);

        linkButton(R.id.HomeResultImageView, MainActivity.class);
        linkButton(R.id.QuestionButton2, ListQuestionActivity.class);
        linkButton(R.id.BackButton, DifficultyActivity.class);

    }
}
package com.example.flashcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
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


        //get intents, conversion to corresponding name for difficulty
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

        //transforming long time to string and adding dot after seconds, then displaying it
        long testTime = intent.getLongExtra("testTime", 0);
        String testTimeText = Long.toString(testTime);
        testTimeText = new StringBuilder(testTimeText).insert(testTimeText.length()-2, ".").toString();
        testTimeText = testTimeText.length()<=3 ? "0" + testTimeText : testTimeText;
        TextView timeTextView = findViewById(R.id.TimeNumberTextView);
        timeTextView.setText(testTimeText + "s");

        //make string of % correct and display it
        String correctResultPercent = String.format(Locale.US, "%.2f", (float) correctAnswerCount * 100 / (float) totalQuestions) + " %";
        TextView resultPercentText = findViewById(R.id.ResultPercentTextView);
        resultPercentText.setText(correctResultPercent);

        //make string of correct / total questions and display it
        String correctResultNumber = correctAnswerCount + " / " + totalQuestions;
        TextView resultNumberText = findViewById(R.id.ResultNumberTextView);
        resultNumberText.setText(correctResultNumber);

        linkButton(R.id.HomeResultImageView, MainActivity.class, false);
        linkButton(R.id.QuestionButton2, ListQuestionActivity.class, false);
        linkButton(R.id.BackButton, MainActivity.class, false);

        updateStats((int)testTime, totalQuestions, correctAnswerCount);


        //write share text
        String shareText = "YOOO JAI FAIT LE TEST SOSIE EN " + difficultyName + " EN " + testTimeText + " SECONDES ET JAI EU " + correctResultNumber;

        shareImageView.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareSub = "I LOVE SOSIE";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        });


    }

    private void updateStats(int quizTime, int answers, int good_answers) {
        SharedPreferences prefs = getSharedPreferences("Stats", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


        editor.putInt("quiz_amount", prefs.getInt("quiz_amount", 0) + 1);
        editor.putInt("total_quiz_time", prefs.getInt("total_quiz_time", 0) + quizTime);
        editor.putInt("total_answers", prefs.getInt("total_answers", 0) + answers);
        editor.putInt("good_answers", prefs.getInt("good_answers", 0) + good_answers);
        editor.apply();
    }
}
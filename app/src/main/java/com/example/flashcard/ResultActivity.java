package com.example.flashcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        String level = intent.getStringExtra("level"); // niveau récupéré depuis l'API
        if (level == null) level = "Facile";

        // Map pour associer le nom du niveau à un index (pour la popup)
        Map<String, Integer> levelMap = new HashMap<>();
        levelMap.put("Facile", 0);
        levelMap.put("Moyen", 1);
        levelMap.put("Difficile", 2);
        levelMap.put("Hardcore", 3);

        // Récupérer l'index correspondant au niveau
        int difficultyIndex = levelMap.getOrDefault(level, 0);

        //transforming long time to string and adding dot after seconds, then displaying it
        long testTime = intent.getLongExtra("testTime", 0);

        // Mettre à jour les stats
        updateStats((int) testTime, totalQuestions, correctAnswerCount);

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
        linkButton(R.id.BackButton, MainActivity.class, false);


        //write share text
        String shareText = "YOOO JAI FAIT LE TEST SOSIE EN " + level + " EN " + testTimeText + " SECONDES ET JAI EU " + correctResultNumber;

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
            onButtonShowPopupWindowClick(view, questions, null, difficultyIndex);
        });

    }

    //simply gets all necessary values and inserts them into shared preferences
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
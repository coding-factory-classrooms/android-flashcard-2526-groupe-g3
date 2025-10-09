package com.example.flashcard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.Timestamp;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StatsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        linkButton(R.id.HomeStatsImageView, MainActivity.class, false);

        //binding views

        TextView totalQuizzesPlayedTextView = findViewById(R.id.TotalQuizTextView);
        TextView totalCorrectTextView = findViewById(R.id.TotalCorrectTextView);
        TextView totalPlayTimeTextView = findViewById(R.id.TotalPlaytimeTextView);
        TextView totalAnsweredTextView = findViewById(R.id.TotalAnsweredTextView);
        TextView averageTimeTextView = findViewById(R.id.AverageTimeTextView);

        //get shared preferences
        SharedPreferences prefs = getSharedPreferences("Stats", MODE_PRIVATE);


        // Protection pour éviter la division par 0 et donc le crash
        int quizAmount = prefs.getInt("quiz_amount", 0);
        int totalQuizTime = prefs.getInt("total_quiz_time", 0);

        String averageTime = quizAmount > 0
                ? TimestampToString(totalQuizTime / quizAmount)
                : "00:00:00";

        //showing texts
        totalQuizzesPlayedTextView.append(Integer.toString(prefs.getInt("quiz_amount", 0)));
        totalCorrectTextView.append(Integer.toString(prefs.getInt("good_answers", 0)));
        totalPlayTimeTextView.append(TimestampToString(prefs.getInt("total_quiz_time", 0)));
        totalAnsweredTextView.append(Integer.toString(prefs.getInt("total_answers", 0)));
        averageTimeTextView.append(averageTime);
    }

    private String TimestampToString(int timestamp) {
        timestamp *= 10;
        return String.format(Locale.FRANCE, "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timestamp),
                TimeUnit.MILLISECONDS.toMinutes(timestamp) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timestamp)),
                TimeUnit.MILLISECONDS.toSeconds(timestamp) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timestamp)));

    }
}
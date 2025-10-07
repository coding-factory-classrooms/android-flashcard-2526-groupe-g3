package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuestionActivity extends AppCompatActivity {

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

        findViewById(R.id.HomeQuestionImageView).setOnClickListener( view->{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.ReponseAButton2).setOnClickListener( view->{
            Intent intent = new Intent(this, ListQuestionActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.ReponseBButton2).setOnClickListener( view->{
            Intent intent = new Intent(this, ListQuestionActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.ReponseCButton2).setOnClickListener( view->{
            Intent intent = new Intent(this, ListQuestionActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.ReponseDButton2).setOnClickListener( view->{
            Intent intent = new Intent(this, ListQuestionActivity.class);
            startActivity(intent);
        });
    }
}
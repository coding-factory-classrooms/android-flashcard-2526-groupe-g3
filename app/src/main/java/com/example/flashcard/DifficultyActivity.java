package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DifficultyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_difficulty);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        linkButton(R.id.HomeDifficultyImageView, MainActivity.class);
        linkButtonWithDifficulty(R.id.EasyImageView, TestActivity.class, 0);
        linkButtonWithDifficulty(R.id.MediumImageView, TestActivity.class, 1);
        linkButtonWithDifficulty(R.id.HardImageView, TestActivity.class, 2);
        linkButtonWithDifficulty(R.id.HardcoreImageView, TestActivity.class, 3);

    }
}
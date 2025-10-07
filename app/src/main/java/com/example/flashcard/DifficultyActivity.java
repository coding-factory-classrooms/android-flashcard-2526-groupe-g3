package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DifficultyActivity extends AppCompatActivity {

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
        findViewById(R.id.HomeDifficultyImageView).setOnClickListener(view->{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.EasyImageView).setOnClickListener(view->{
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.MediumImageView).setOnClickListener(view->{
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.HardImageView).setOnClickListener(view->{
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.HardcoreImageView).setOnClickListener(view->{
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        });
    }
}
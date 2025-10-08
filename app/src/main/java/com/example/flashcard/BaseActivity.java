package com.example.flashcard;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected void linkButton(int id, Class<?> targetActivity) {
        View button = findViewById(id);
        button.setTag(targetActivity);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, (Class<?>) v.getTag()));
    }
}


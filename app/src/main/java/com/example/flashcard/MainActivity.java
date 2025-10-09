package com.example.flashcard;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        linkButton(R.id.StartButton, DifficultyActivity.class);
        linkButton(R.id.AboutButton, AboutActivity.class);

        List<Integer> difficultyList = new ArrayList<>();
        for(int i = 0; i<4; i++){
            difficultyList.add(i);
        }

        findViewById(R.id.QuestionButton).setOnClickListener(view ->{
            onButtonShowPopupWindowClick(view, null, difficultyList, -1);
        });
    }
}
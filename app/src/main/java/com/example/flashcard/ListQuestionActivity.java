package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListQuestionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        linkButton(R.id.HomeListQuestionImageView, MainActivity.class);


        ArrayList<Question> questions = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            int ImageID = getResources().getIdentifier("question1" , "drawable", getPackageName());
            List<String> a = new ArrayList<>();
            a.add("Paris");
            a.add("Londres");
            a.add("Berlin");
            a.add("Madrid");
            questions.add(new Question("question1", a, 0, ImageID));
        }

        // On branche tout le monde
        // Les données de l'adaptater
        // l'adaptater au recycleview
        QuestionAdaptater adaptater = new QuestionAdaptater(questions);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adaptater);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
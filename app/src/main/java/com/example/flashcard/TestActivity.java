package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends BaseActivity {

    private int currentDifficulty;
    private int currentQuestion = 0;
    private ImageView questionImage;
    private LinearLayout answersContainer;
    private final List<Difficulty> difficultiesList = new ArrayList<>();
    private final Map<String, Integer> imageMap = new HashMap<>();
    private int correctAnswerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        linkButton(R.id.HomeTestImageView, MainActivity.class);

        currentDifficulty = getIntent().getIntExtra("difficulty_index", 0);
        questionImage = findViewById(R.id.PeopleImageView);
        answersContainer = findViewById(R.id.answersContainer);

        initImageMap();

        String json = JsonUtils.readJsonFromRaw(this, R.raw.questions);

        try {
            JSONObject root = new JSONObject(json);
            JSONArray difficultiesArray = root.getJSONArray("difficulties");

            for (int i = 0; i < difficultiesArray.length(); i++) {
                JSONObject difficultyObj = difficultiesArray.getJSONObject(i);
                String level = difficultyObj.getString("level");

                JSONArray questionsArray = difficultyObj.getJSONArray("questions");
                List<Question> questionsList = new ArrayList<>();

                for (int j = 0; j < questionsArray.length(); j++) {
                    JSONObject questionObj = questionsArray.getJSONObject(j);
                    String imageId = questionObj.getString("image_id");

                    JSONArray answersArray = questionObj.getJSONArray("answers");
                    List<String> answersList = new ArrayList<>();
                    for (int k = 0; k < answersArray.length(); k++) {
                        answersList.add(answersArray.getString(k));
                    }

                    int correctIndex = questionObj.getInt("correct");
                    questionsList.add(new Question(imageId, answersList, correctIndex));
                }

                difficultiesList.add(new Difficulty(level, questionsList));
            }
        } catch (JSONException e) {
            Log.e("TestActivity", "Erreur parsing JSON", e);
        }

        showQuestion(difficultiesList.get(currentDifficulty).questions.get(currentQuestion));

    }

    private void initImageMap() {
        imageMap.clear();

        try {
            Class<?> drawableClass = R.drawable.class;
            java.lang.reflect.Field[] fields = drawableClass.getFields();

            for (java.lang.reflect.Field field : fields) {
                String name = field.getName();
                if (name.startsWith("question")) { // ne prendre que les images "question*"
                    int resId = field.getInt(null);
                    imageMap.put(name, resId);
                }
            }
        } catch (Exception e) {
            Log.e("TestActivity", "Erreur", e);
        }
    }


    private void showQuestion(Question question) {
        Integer imageResId = imageMap.get(question.image);
        if (imageResId != null) {
            questionImage.setImageResource(imageResId);
        } else {
            questionImage.setImageDrawable(null);
        }

        answersContainer.removeAllViews();

        TextView currentQuestionTextView = findViewById(R.id.CurrentQuestionTextView);
        String currentQuestionText = ("Question " + (currentQuestion+1) + " / " + difficultiesList.get(currentDifficulty).questions.toArray().length);
        currentQuestionTextView.setText(currentQuestionText);

        for (int i = 0; i < question.answers.size(); i++) {
            final int index = i;
            Button answerButton = new Button(this);
            answerButton.setText(question.answers.get(i));
            answerButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            answerButton.setOnClickListener(v -> checkAnswer(index));
            answersContainer.addView(answerButton);
        }
    }


    private void checkAnswer(int selectedIndex) {
        Question question = difficultiesList.get(currentDifficulty).questions.get(currentQuestion);

        if (selectedIndex == question.correct) {
            correctAnswerCount++;
            Toast.makeText(this, "Bonne réponse", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mauvaise réponse", Toast.LENGTH_SHORT).show();
        }

        currentQuestion++;
        if (currentQuestion < difficultiesList.get(currentDifficulty).questions.size()) {
            showQuestion(difficultiesList.get(currentDifficulty).questions.get(currentQuestion));
        } else {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("correctAnswerCount", correctAnswerCount);
            intent.putExtra("totalQuestions", currentQuestion);
            intent.putExtra("difficulty", currentDifficulty);

            startActivity(intent);
            finish();
        }
    }
}

package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        parseJsonData(json);

        showQuestion(difficultiesList.get(currentDifficulty).questions.get(currentQuestion));

    }

    private void parseJsonData(String jsonData) {
        try {
            JSONObject rootObject = new JSONObject(jsonData);
            JSONArray difficultyArray = rootObject.getJSONArray("difficulties");

            for (int difficultyIndex = 0; difficultyIndex < difficultyArray.length(); difficultyIndex++) {
                JSONObject difficultyJson = difficultyArray.getJSONObject(difficultyIndex);
                String difficultyLevel = difficultyJson.getString("level");

                JSONArray questionArray = difficultyJson.getJSONArray("questions");
                List<Question> questionList = new ArrayList<>();

                for (int questionIndex = 0; questionIndex < questionArray.length(); questionIndex++) {
                    JSONObject questionJson = questionArray.getJSONObject(questionIndex);
                    String imageName = questionJson.getString("image_id");

                    JSONArray answerArray = questionJson.getJSONArray("answers");
                    List<String> answerList = new ArrayList<>();

                    for (int answerIndex = 0; answerIndex < answerArray.length(); answerIndex++) {
                        answerList.add(answerArray.getString(answerIndex));
                    }

                    int correctAnswerIndex = questionJson.getInt("correct");
                    int id = getResources().getIdentifier(imageName,"drawable", getPackageName());
                    questionList.add(new Question(imageName, answerList, correctAnswerIndex, id));
                }

                difficultiesList.add(new Difficulty(difficultyLevel, questionList));
            }

        } catch (JSONException e) {
            Log.e("TestActivity", "Erreur lors du parsing du JSON", e);
        }
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
            Toast.makeText(this, "Bonne réponse", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mauvaise réponse", Toast.LENGTH_SHORT).show();
        }

        currentQuestion++;
        if (currentQuestion < difficultiesList.get(currentDifficulty).questions.size()) {
            showQuestion(difficultiesList.get(currentDifficulty).questions.get(currentQuestion));
        } else {
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListQuestionActivity extends BaseActivity{

    public ArrayList<Question> questionsList = new ArrayList<>();
    public ArrayList<Question> tempQuestionsList = new ArrayList<>();
    private final List<Difficulty> difficultiesList = new ArrayList<>();

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

        List<Integer> difficultyNameList = new ArrayList<>();
        for(int i = 0; i<4; i++){
            difficultyNameList.add(i);
        }

        findViewById(R.id.MenuImageView).setOnClickListener(view ->{
            onButtonShowPopupWindowClick(view, null, difficultyNameList, -1);
        });

        Intent srcintent = getIntent();

        if(srcintent.getParcelableArrayListExtra("listQuestion") == null ) {
            String json = JsonUtils.readJsonFromRaw(this, R.raw.questions);
            parseJsonData(json);
            List<Question> questions = difficultiesList.get(srcintent.getIntExtra("difficulty", 0)).questions;
            questionsList = new ArrayList<>(questions);
        }
        else{
            questionsList = srcintent.getParcelableArrayListExtra("listQuestion");
        }

        // Link the adaptater and the recycler
        QuestionAdaptater adaptater = new QuestionAdaptater(questionsList);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adaptater);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //Add the Jsonfile in an object
    public void parseJsonData(String jsonData) {
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

}
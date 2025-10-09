package com.example.flashcard;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionActivity extends BaseActivity {

    ImageView imageQuestion;
    List<String> answersList;
    Toast toast;

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

        linkButton(R.id.HomeQuestionImageView, MainActivity.class, false);

        //Create all the intent
        Intent srcIntent = getIntent();
        Intent intent = new Intent(this, ListQuestionActivity.class);

        //Take the variable named question
        Question question =srcIntent.getParcelableExtra("Question");

        LinearLayout answersLayout = findViewById(R.id.QuestionLineaLayout);

        imageQuestion = findViewById(R.id.PeopleImageView2);
        answersList = question.answers;

        //If there's no image, put nothing
        Log.d("QuestionActivity", ""+question.id);
        if (question.id != 0) {
            imageQuestion.setImageResource(question.id);
        } else {
            imageQuestion.setImageResource(R.drawable.not_found);
        }

        List<Integer> answerIndexes = new ArrayList<>();
        for (int i = 0; i < question.answers.size(); i++) {
            answerIndexes.add(i);
        }

        Collections.shuffle(answerIndexes);
        imageQuestion.setOnClickListener(v -> {
            // Rien faire si l'image est celle par défaut
            if (question.id == 0) {
                return;
            }
            // Affiche le dialog de zoom
            showZoomDialog(question.id);
        });

        for(int i = 0; i < answersList.size(); i++){
            final int originalIndex = answerIndexes.get(i);

            // Create a button with the response and add it to the view
            addButton(answersLayout, question.answers.get(originalIndex), () -> {
                //Add a different toast if it's the good response or the wrong
                if(originalIndex != question.correct){
                    toast = Toast.makeText(this, "Mauvaise réponse, la bonne réponse " +
                            "était : "+question.answers.get(question.correct)+".", Toast.LENGTH_SHORT);
                }
                else{
                    toast = Toast.makeText(this, "Bonne réponse !", Toast.LENGTH_SHORT);
                }

                toast.show();
                startActivity(intent);
                finish();
            });

        }
    }

    public void showZoomDialog(int imageResId) {
        // Créer Dialog
        final android.app.Dialog zoomDialog = new android.app.Dialog(this);

        // Défini le layout
        zoomDialog.setContentView(R.layout.dialog_zoom_image);

        // Rend la fenêtre du dialog transparente pour voir le fond du layout
        if (zoomDialog.getWindow() != null) {
            zoomDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Récupère l'ImageView à l'intérieur du dialog
        ImageView zoomedImageView = zoomDialog.findViewById(R.id.zoomedImageView);
        zoomedImageView.setImageResource(imageResId);

        // Ferme le dialog si utilisateur clique sur l'image zoomée
        zoomedImageView.setOnClickListener(v -> zoomDialog.dismiss());

        // Affiche le dialog
        zoomDialog.show();
    }
}
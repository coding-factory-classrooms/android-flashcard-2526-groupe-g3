package com.example.flashcard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
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

import java.time.Instant;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends BaseActivity {

    // variable pour la difficulté actuelle du quiz
    private int currentDifficulty;
    // compteur pour passer les questions
    private int currentQuestion = 0;
    // Image qui sert pour les questions
    private ImageView questionImage;
    // Le layout pour afficher les réponses sous forme de boutons
    private LinearLayout answersContainer;
    // la liste qui correspond au json des questions classés par difficulté.
    private final List<Difficulty> difficultiesList = new ArrayList<>();
    // Une Map pour associer les noms des images a leur ressources dans drawable
    private final Map<String, Integer> imageMap = new HashMap<>();
    // Compteur pour compter les bonnes réponses
    private final ArrayList<Question> failedQuestion = new ArrayList<>();
    private int correctAnswerCount;
    long startTime;
    // Booléen pour activer le mode TimeAttack
    private boolean isTimeAttack = false;
    // variable pour créer le timer pour le TimeAttack
    CountDownTimer timer;
    // UI du timer
    private TextView timerTextView;

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

        startTime = System.currentTimeMillis() / 10;
        linkButton(R.id.HomeTestImageView, MainActivity.class, false);

        // On récupère la dificulté avec l'index transmis par DifficultyActivity
        currentDifficulty = getIntent().getIntExtra("difficulty_index", 0);

        // On récupère les éléments de l'UI
        questionImage = findViewById(R.id.PeopleImageView);
        answersContainer = findViewById(R.id.answersContainer);

        // Initialisation de la map
        initImageMap();

        // On charge le JSON sous format String
        String json = JsonUtils.readJsonFromRaw(this, R.raw.questions);

        // Fonction qui parse le json pour remplir la liste des difficultés et des questions
        parseJsonData(json);

        // On récupère le flag donné par DifficultyActivity
        isTimeAttack = getIntent().getBooleanExtra("isTimeAttack", false);

        // On récupère l'UI du timer
        timerTextView = findViewById(R.id.TimerTextView);

        // On cache ou affiche le TextView selon le mode TimeAttack
        if (isTimeAttack) {
            timerTextView.setVisibility(View.VISIBLE);
        } else {
            timerTextView.setVisibility(View.GONE);
        }

        // Recuperation de la liste de questions de la difficulté actuelle
        List<Question> questionsToShuffle = difficultiesList.get(currentDifficulty).questions;
        // On melange la liste
        Collections.shuffle(questionsToShuffle);

        showQuestion(difficultiesList.get(currentDifficulty).questions.get(currentQuestion));

    }

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

    // une fonction qui parcourt toutes les ressources drawable de l'app,
    // on vérifie si le nom de l'image commence par "question*"
    // (* correspond au numéro de la question) et on l'ajoute à la map
    private void initImageMap() {
        // on vide la map
        imageMap.clear();
        try {
            // on récupère la classe drawable qui a toutes les images
            Class<?> drawableClass = R.drawable.class;
            // et on récupère toutes les images de cette classe (méthode trouvé sur internet)
            java.lang.reflect.Field[] fields = drawableClass.getFields();

            // on parcourt toutes les images en récupérant nom (String name) et on l'ajoute a la map
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

    // fonction principale pour afficher les questions (le système de quiz enfaite)
    private void showQuestion(Question question) {

        // si mode TimeAttack on démarre le timer
        if (isTimeAttack) {
            startTimer();
        }

        // on récupère l'ID de l'image qui correspond a la question depuis la map
        Integer imageResId = imageMap.get(question.image);
        if (imageResId != null) {
            questionImage.setImageResource(imageResId);
        } else {
            questionImage.setImageResource(R.drawable.not_found);
        }

        questionImage.setOnClickListener(v -> {
            // Rien faire si l'image est celle par défaut
            if (question.id == 0) {
                return;
            }
            // Affiche le dialog de zoom
            showZoomDialog(question.id);
        });

        // on supprime tout les boutons (au cas où ils sont encore la)
        answersContainer.removeAllViews();

        // Creer une liste des indexes des réponses
        List<Integer> answerIndexes = new ArrayList<>();
        // on récupère le textview qui affiche le numéro de la question (il faut le replacer sur l'UI)
        TextView currentQuestionTextView = findViewById(R.id.CurrentQuestionTextView);
        String currentQuestionText = ("Question " + (currentQuestion+1) + " / " + difficultiesList.get(currentDifficulty).questions.toArray().length);
        currentQuestionTextView.setText(currentQuestionText);

        for (int i = 0; i < question.answers.size(); i++) {
            answerIndexes.add(i);
        }

        // Mélange la liste
        Collections.shuffle(answerIndexes);

        // on créer dynamiquements les boutons réponses (c'est ici qu'on les designs)
        for (int i = 0; i < answerIndexes.size(); i++) {
            // Index original
            final int originalIndex = answerIndexes.get(i); // index de la réponse
            Button answerButton = new Button(this); // on crée le bouton
            answerButton.setText(question.answers.get(originalIndex)); // UI du bouton
            answerButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // au clique on check la réponse
            answerButton.setOnClickListener(v -> checkAnswer(originalIndex));

            // on ajoute le bouton a l'UI
            answersContainer.addView(answerButton);
        }
    }

    // fonction pour vérifier si la réponse est correcte ou non
    private void checkAnswer(int selectedIndex) {

        // on récupère la question actuelle selon la difficulté sélectionné, et l'index
        Question question = difficultiesList.get(currentDifficulty).questions.get(currentQuestion);

        // on vérifie si la réponse est correcte ou non
        if (selectedIndex == question.correct) {
            // on incrémente le compteur si la réponse est correcte
            correctAnswerCount++;
            Toast.makeText(this, "Bonne réponse", Toast.LENGTH_SHORT).show();
        } else {
            failedQuestion.add(question);
            Toast.makeText(this, "Mauvaise réponse", Toast.LENGTH_SHORT).show();
        }
        nextQuestion();
    }

    // fonction pour le fonctionnement du timer du TimeAttack
    private void startTimer() {


        if (!isTimeAttack) return;

        // Durée des timers : facile, moyen, difficle, hardcore
        long[] questionDurations = {15000, 10000, 7000, 5000};
        // on récupère le timer qui correspond a la difficulté sélectionnée
        long currentQuestionDuration = questionDurations[currentDifficulty];


        timerTextView.setText("Temps restants : " + (currentQuestionDuration / 1000) + "s");
        timerTextView.setVisibility(View.VISIBLE);

        // Annuler l’ancien timer si existant
        if (timer != null) {
            timer.cancel();
        }

        // on crée et on démarre le timer
        timer = new CountDownTimer(currentQuestionDuration, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisRemaining) {
                // on met a jour l'élement UI avec le temps restants (on le transforme en secondes)
                long secondsRemaining = (millisRemaining + 999) / 1000;
                timerTextView.setText("Temps restants : " + secondsRemaining + "s");
            }


            // Fonction de fin de questionnaire, ou de passage a la question suivante
            @Override
            public void onFinish() {
                Toast.makeText(TestActivity.this, "Temps écoulé", Toast.LENGTH_SHORT).show();
                nextQuestion();
            }
        }.start();
    }

    private void nextQuestion() {
        currentQuestion++;
        if (currentQuestion < difficultiesList.get(currentDifficulty).questions.size()) {
            showQuestion(difficultiesList.get(currentDifficulty).questions.get(currentQuestion));
        } else {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("correctAnswerCount", correctAnswerCount);
            intent.putExtra("totalQuestions", currentQuestion);
            intent.putExtra("difficulty", currentDifficulty);
            intent.putParcelableArrayListExtra("failedQuestion", failedQuestion);
            long testTime = (System.currentTimeMillis() / 10) - startTime;
            intent.putExtra("testTime", testTime);

            startActivity(intent);
            finish();
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

    // on override la fonction finish pour qu'au càs où il y'a un problème avec le timer ça le stoppe
    @Override
    public void finish() {
        if (timer != null) {
            timer.cancel();
        }
        super.finish();
    }
}

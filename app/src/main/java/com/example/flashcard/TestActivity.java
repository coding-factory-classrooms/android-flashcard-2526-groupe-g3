package com.example.flashcard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends BaseActivity {

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

    // Niveau de base
    private String level = "Facile";

    // Durée du timer par difficulté
    private static final Map<String, Long> difficultyDurations = new HashMap<>() {{
        put("Facile", 15000L);
        put("Moyen", 10000L);
        put("Difficile", 7000L);
        put("Hardcore", 5000L);
    }};

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

        // On récupère les éléments de l'UI
        questionImage = findViewById(R.id.PeopleImageView);
        answersContainer = findViewById(R.id.answersContainer);
        timerTextView = findViewById(R.id.TimerTextView);

        // On récupère le flag donné par DifficultyActivity
        isTimeAttack = getIntent().getBooleanExtra("isTimeAttack", false);
        // On cache ou affiche le TextView selon le mode TimeAttack
        timerTextView.setVisibility(isTimeAttack ? View.VISIBLE : View.GONE);

        // Initialisation de la map
        initImageMap();

        // On récupère le niveau de difficulté
        level = getIntent().getStringExtra("level");
        if (level == null) level = "Facile";

        // Charger les questions depuis l’API
        fetchQuestionsFromApi(level);

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
            Log.e("TestActivity", "Erreur initImageMap", e);
        }
    }

    // fonction principale pour afficher les questions (le système de quiz enfaite)
    @SuppressLint("SetTextI18n")
    private void showQuestion(Question question) {

        // si mode TimeAttack on démarre le timer
        if (isTimeAttack) startTimer();


        // on récupère l'ID de l'image qui correspond a la question depuis la map
        Integer imageResId = imageMap.get(question.image);
        if (imageResId != null) questionImage.setImageResource(imageResId);
        else questionImage.setImageResource(R.drawable.not_found);

        // Cliquer sur l'image pour zoomer
        questionImage.setOnClickListener(v -> {
            if (question.id != 0) showZoomDialog(question.id);
        });

        // on supprime tout les boutons (au cas où ils sont encore la)
        answersContainer.removeAllViews();

        // Creer une liste des indexes des réponses
        List<Integer> answerIndexes = new ArrayList<>();
        // on récupère le textview qui affiche le numéro de la question (il faut le replacer sur l'UI)
        TextView currentQuestionTextView = findViewById(R.id.CurrentQuestionTextView);
        currentQuestionTextView.setText("Question " + (currentQuestion + 1) + " / " +
                difficultiesList.get(0).questions.size());
        currentQuestionTextView .setTextColor(Color.BLACK);

        for (int i = 0; i < question.answers.size(); i++) answerIndexes.add(i);
        Collections.shuffle(answerIndexes);

        // on créer dynamiquements les boutons réponses (c'est ici qu'on les designs)
        for (int i = 0; i < answerIndexes.size(); i++) {
            final int originalIndex = answerIndexes.get(i); // index de la réponse
            AppCompatButton answerButton = new AppCompatButton(this); // on crée le bouton
            answerButton.setText(question.answers.get(originalIndex)); // UI du bouton
            answerButton.setTextColor(Color.WHITE);
            answerButton.setBackgroundResource(R.drawable.button_png);
            answerButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    150
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
        Question question = difficultiesList.get(0).questions.get(currentQuestion);

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
    @SuppressLint("SetTextI18n")
    private void startTimer() {


        if (!isTimeAttack) return;

        // Durée des timers : facile, moyen, difficle, hardcore
        long currentQuestionDuration = difficultyDurations.getOrDefault(level, 15000L);

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

    // Passer à la question suivante ou finir le quiz
    private void nextQuestion() {

        // On passe à la question suivante
        currentQuestion++;

        // si y a encore des questions on affiche la question suivante sinon on envoie la vue ResultActivity
        if (currentQuestion < difficultiesList.get(0).questions.size()) {
            showQuestion(difficultiesList.get(0).questions.get(currentQuestion));
        } else {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("correctAnswerCount", correctAnswerCount);
            intent.putExtra("totalQuestions", currentQuestion);
            intent.putParcelableArrayListExtra("failedQuestion", failedQuestion);
            long testTime = (System.currentTimeMillis() / 10) - startTime;
            intent.putExtra("testTime", testTime);

            startActivity(intent);
            finish();
        }
    }

    private void fetchQuestionsFromApi(String level) {

        // on crée un client htpp pour les requêtes
        OkHttpClient client = new OkHttpClient();

        String url = "https://students.gryt.tech/api/L2/sosies/";

        // requête GET
        Request request = new Request.Builder().url(url).build();

        // requête asynchrone
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(TestActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Il faut que la réponse est un contenu
                if (response.isSuccessful() && response.body() != null) {
                    //On récupère le json
                    String json = response.body().string();

                    // On utilise Gson pour parser le JSON en objet Java
                    Gson gson = new Gson();
                    APIElements.ApiResponse apiResponse = gson.fromJson(json, APIElements.ApiResponse.class);

                    // Chercher la difficulté sélectionnée
                    APIElements.ApiDifficulty selectedDifficulty = null;
                    for (APIElements.ApiDifficulty diff : apiResponse.difficulties) {
                        if (diff.level.equalsIgnoreCase(level)) {
                            selectedDifficulty = diff;
                            break;
                        }
                    }

                    // Si le level actuelle n'a pas de questions on finish
                    if (selectedDifficulty == null || selectedDifficulty.questions == null || selectedDifficulty.questions.isEmpty()) {
                        runOnUiThread(() -> {
                            Toast.makeText(TestActivity.this, "Aucune question disponible pour ce niveau.", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                        return;
                    }

                    // Conversion des questions API en objets Question
                    List<Question> questionList = new ArrayList<>();
                    for (APIElements.ApiQuestion apiQ : selectedDifficulty.questions) {
                        // On récupère l'ID de l'image depuis la map
                        int id = imageMap.getOrDefault(apiQ.image_id, 0);
                        // On crée un objet Question et on le met dans la liste
                        questionList.add(new Question(apiQ.image_id, apiQ.answers, apiQ.correct, id));
                    }

                    // on vide la liste (au càs où) et on ajoute la nouvelle
                    difficultiesList.clear();
                    difficultiesList.add(new Difficulty(level, questionList));

                    // Comme on est en thread réseau, on doit mettre à jour l'UI dans runOnUiThread
                    runOnUiThread(() -> {
                        currentQuestion = 0; // Première question
                        // On mélange
                        Collections.shuffle(difficultiesList.get(0).questions);
                        // On affiche
                        showQuestion(difficultiesList.get(0).questions.get(currentQuestion));
                    });
                }
            }
        });
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

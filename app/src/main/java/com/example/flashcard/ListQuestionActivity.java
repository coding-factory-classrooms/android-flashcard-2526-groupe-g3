package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListQuestionActivity extends BaseActivity{

    // Liste des questions à afficher
    public ArrayList<Question> questionsList = new ArrayList<>();

    // Liste temporaire si besoin
    public ArrayList<Question> tempQuestionsList = new ArrayList<>();

    // Liste des difficultés
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

        linkButton(R.id.HomeListQuestionImageView, MainActivity.class, false);

        // On crée la liste des indexes
        List<Integer> difficultyNameList = new ArrayList<>();
        for(int i = 0; i<4; i++){
            difficultyNameList.add(i);
        }

        // Le Pop du choix de difficulté
        findViewById(R.id.MenuImageView).setOnClickListener(view ->{
            onButtonShowPopupWindowClick(view, null, difficultyNameList, -1);
        });

        // Link the adaptater and the recycler
        QuestionAdaptater adaptater = new QuestionAdaptater(questionsList);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adaptater);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Récupérer la difficulté passée depuis MainActivity
        int difficultyIndex = getIntent().getIntExtra("difficulty", 0);

        // Récupérer les questions correspondant à cette difficulté depuis l'API
        fetchQuestionsByDifficulty(difficultyIndex);

    }

    // Méthode pour récupérer les questions depuis l'API selon la difficulté
    private void fetchQuestionsByDifficulty(int difficultyIndex) {

        // Tableau de noms des levels
        String[] levels = {"Facile", "Moyen", "Difficile", "Hardcore"};
        String level = levels[difficultyIndex];

        // Création du client HTTP pour la requête
        OkHttpClient client = new OkHttpClient();
        String url = "https://students.gryt.tech/api/L2/sosies/";

        // Requête GET
        Request request = new Request.Builder().url(url).build();

        // Requête asynchrone
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ListQuestionActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // La réponse ne doit pas être vide

                if (response.isSuccessful() && response.body() != null) {
                    // on récupère le json
                    String json = response.body().string();
                    // on utilise GSON pour la conversion en objet Java
                    Gson gson = new Gson();
                    APIElements.ApiResponse apiResponse = gson.fromJson(json, APIElements.ApiResponse.class);

                    // Chercher la difficulté correspondant au level demandé
                    APIElements.ApiDifficulty selectedDifficulty = null;
                    for (APIElements.ApiDifficulty diff : apiResponse.difficulties) {
                        if (diff.level.equalsIgnoreCase(level)) {
                            selectedDifficulty = diff;
                            break;
                        }
                    }
                    // Si le level n'a pas de questions, erreur
                    if (selectedDifficulty == null || selectedDifficulty.questions == null || selectedDifficulty.questions.isEmpty()) {
                        runOnUiThread(() ->
                                Toast.makeText(ListQuestionActivity.this, "Aucune question disponible pour ce niveau.", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    // Préparer une Map<String, Integer> contenant tous les drawables question*
                    Map<String, Integer> imageMap = new HashMap<>();
                    try {
                        // On récupère toutes les ressources dans drawable
                        Class<?> drawableClass = R.drawable.class;
                        java.lang.reflect.Field[] fields = drawableClass.getFields();

                        // on parcourt chaque champ
                        for (java.lang.reflect.Field field : fields) {
                            String name = field.getName(); // non du drawable
                            if (name.startsWith("question")) { // ne prendre que les images question*
                                int resId = field.getInt(null);
                                imageMap.put(name, resId); // ajout dans la map
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Ensuite, remplir la liste des questions à afficher
                    questionsList.clear(); // on vide
                    for (APIElements.ApiQuestion apiQ : selectedDifficulty.questions) {
                        // Récupérer l'ID à partir de la map au lieu de getIdentifier
                        // Utilise une image par défaut si le drawable n'existe pas
                        int id = imageMap.getOrDefault(apiQ.image_id, R.drawable.not_found);

                        // On crée l'objet
                        questionsList.add(new Question(apiQ.image_id, apiQ.answers, apiQ.correct, id));
                    }

                    runOnUiThread(() -> {
                        // Notifier l'adaptateur existant
                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        QuestionAdaptater adaptater = (QuestionAdaptater) recyclerView.getAdapter();
                        if (adaptater != null) {
                            adaptater.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
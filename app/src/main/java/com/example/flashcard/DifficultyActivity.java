package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DifficultyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_difficulty);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Récupère le flag "isTimeAttack" passé depuis l'activité précédente et indique avec le booléen
        // si on lance en timeAttack ou non
        boolean isTimeAttack = getIntent().getBooleanExtra("isTimeAttack", false);

        // Tableau des IDs des boutons correspondant aux différentes difficultés (c'est plus conventionnel)
        int[] difficultyButtons = {
                R.id.EasyImageView,
                R.id.MediumImageView,
                R.id.HardImageView,
                R.id.HardcoreImageView
        };

        // Boucle sur chaque bouton pour lui associer un clic (méthode trouvé sur internet)
        for (int i = 0; i < difficultyButtons.length; i++) {
            linkButtonWithDifficulty(difficultyButtons[i], TestActivity.class, i, isTimeAttack);
        }

        linkButton(R.id.HomeDifficultyImageView, MainActivity.class, false);
    }
}
package com.example.flashcard;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


// Classe de base que toutes les autres activités vont étendre.
// Elle permet de factoriser des fonctions communes (comme la navigation entre activités).
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    // Fonction pour reliéun bouton à une activité
    // + le timer est arrêter avant de quitté si on est dans TestActivity
    protected void linkButton(int id, Class<?> targetActivity, boolean isTimeAttack) {
        View button = findViewById(id);
        button.setOnClickListener(v -> {
            // Si l'activité actuelle est TestActivity on arrête le timer avant de changer d'écran
            if (this instanceof TestActivity) {
                TestActivity testActivity = (TestActivity) this;
                if (testActivity.timer != null) {
                    testActivity.timer.cancel();
                }
            }
            // On crée un Intent pour aller vers l'activité cible
            Intent intent = new Intent(this, targetActivity);
            // on envoie aussi le booléen TimeAttack
            intent.putExtra("isTimeAttack", isTimeAttack);
            startActivity(intent);
            finish();
        });
    }

    // Même fonction qu'au dessus mais on envoie aussi la difficulté, utile pour l'écran de sélection de difficulté
    protected void linkButtonWithDifficulty(int buttonId, Class<?> activityClass, int difficultyIndex, boolean isTimeAttack) {
        findViewById(buttonId).setOnClickListener(v -> {
            // On crée l’intent avec les informations de difficulté et du mode TimeAttack
            Intent intent = new Intent(this, activityClass);
            intent.putExtra("difficulty_index", difficultyIndex);
            intent.putExtra("isTimeAttack", isTimeAttack);
            startActivity(intent);
        });
    }

    // Méthode par défaut appelée lors du clic sur une vue (si tag défini)
    @Override
    public void onClick(View v) {
        // Lance l’activité dont la classe est stockée dans le tag du bouton
        startActivity(new Intent(this, (Class<?>) v.getTag()));
    }
}


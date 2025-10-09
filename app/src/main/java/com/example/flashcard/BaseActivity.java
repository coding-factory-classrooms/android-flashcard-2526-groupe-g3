package com.example.flashcard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected void linkButton(int id, Class<?> targetActivity) {
        View button = findViewById(id);
        button.setTag(targetActivity);
        button.setOnClickListener(this);
    }

    protected void linkButtonWithDifficulty(int buttonId, Class<?> activityClass, int difficultyIndex) {
        findViewById(buttonId).setOnClickListener(v -> {
            Intent intent = new Intent(this, activityClass);
            intent.putExtra("difficulty_index", difficultyIndex);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, (Class<?>) v.getTag()));
    }

    public void onButtonShowPopupWindowClick(View view, @Nullable ArrayList<Question> questions, @Nullable List<Integer> difficultyList, int difficulty) {

        ArrayList<String> difficultyName = new ArrayList<>();
        difficultyName.add("Simple");
        difficultyName.add("Moyen");
        difficultyName.add("Difficile");
        difficultyName.add("Hardcore");

        // Take view and instanciate the layout pop up in the view
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.pop_up, null);


        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popUpWindow = new PopupWindow(popUpView, width, height, focusable);

        // Show The Popup Window and make it appear without taking the other element
        popUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Find the button to close and the layout to add button on it
        ImageView closePopUp = popUpView.findViewById(R.id.ClosePopUpimageView);
        LinearLayout popUpLayout = popUpView.findViewById(R.id.popUpLayout);
        Intent intent = new Intent(this, ListQuestionActivity.class);

        // Close the pop up when the cross is clicked
        closePopUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View popUpView) {
                popUpWindow.dismiss();
            }
        });

        if(questions != null){
            addButton(popUpLayout, "Questions ratés", () -> {
                intent.putParcelableArrayListExtra("listQuestion", questions);
                startActivity(intent);
            });
        }
        if(difficultyList != null) {
            for(int i = 0; i < difficultyList.size(); i++){
                final int j = i;
                addButton(popUpLayout, difficultyName.get(i), () -> {
                    intent.putExtra("difficulty", j);
                    startActivity(intent);
                });

            }
        }
        if(difficulty != -1) {
            addButton(popUpLayout, difficultyName.get(difficulty), () -> {
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            });
        }
    }

    //Create a function where you can put things on
    public void addButton(LinearLayout parent, String text, Runnable onClick) {
        // Create a button with the response and add it to the view
        Button button = new Button(this);
        button.setText(text);
        parent.addView(button);

        //When it's click it runs the code you put in
        button.setOnClickListener(v -> onClick.run());
    }
}




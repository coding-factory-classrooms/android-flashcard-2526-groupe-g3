package com.example.flashcard;

import java.util.List;

public class Question {
    public String image;
    public List<String> answers;
    public int correct;

    public Question(String image, List<String> answers, int correct) {
        this.image = image;
        this.answers = answers;
        this.correct = correct;
    }
}

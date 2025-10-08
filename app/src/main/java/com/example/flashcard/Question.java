package com.example.flashcard;

import java.util.List;

public class Question {

    public int id;
    public String image;
    public List<String> answers;
    public int correct;

    public Question(String image, List<String> answers, int correct, int id) {
        this.image = image;
        this.answers = answers;
        this.correct = correct;
        this.id = id;
    }
}

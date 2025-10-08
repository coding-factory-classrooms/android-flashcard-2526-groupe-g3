package com.example.flashcard;

import java.util.List;

public class Difficulty {
    public String level;
    public List<Question> questions;

    public Difficulty(String level, List<Question> questions) {
        this.level = level;
        this.questions = questions;
    }
}

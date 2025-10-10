package com.example.flashcard;

import java.util.List;

public class APIElements {

    public static class ApiQuestion {
        public String image_id;
        public List<String> answers;
        public int correct;
    }

    public static class ApiDifficulty {
        public String level;
        public List<ApiQuestion> questions;
    }

    public static class ApiResponse {
        public List<ApiDifficulty> difficulties;
    }
}

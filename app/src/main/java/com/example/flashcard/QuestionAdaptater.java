package com.example.flashcard;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class QuestionAdaptater extends RecyclerView.Adapter<QuestionAdaptater.ViewHolder> {

    public QuestionAdaptater(ArrayList<Question> questions) {this.questions = questions;}

    ArrayList<Question> questions;
    Context context;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.activity_question_image, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdaptater.ViewHolder holder, int position) {
        Question question = questions.get(position);

        holder.questionImageView.setImageResource(ImageID);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView questionImageView;
        Button questionActivityButton;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            questionImageView = itemView.findViewById(R.id.QuestionImageView);
            questionActivityButton= itemView.findViewById(R.id.QuestionActivityButton);
        }
    }
}

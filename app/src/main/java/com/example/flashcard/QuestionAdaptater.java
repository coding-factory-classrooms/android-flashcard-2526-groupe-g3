package com.example.flashcard;



import static androidx.core.content.ContextCompat.startActivities;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
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
public class QuestionAdaptater extends RecyclerView.Adapter<QuestionAdaptater.ViewHolder>{

    public QuestionAdaptater(ArrayList<Question> questions) {this.questions = questions;}

    ArrayList<Question> questions;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.activity_question_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //This function
    @Override
    public void onBindViewHolder(@NonNull QuestionAdaptater.ViewHolder holder, int position) {
        //Create the line of the list of question
        Question question = questions.get(position);
        holder.questionImageView.setImageResource(question.id);

        //Next line add the Question to the function LinkData
        holder.linkData(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView questionImageView;
        Button questionActivityButton;
        Question clickedQuestion;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            Context context = itemView.getContext();
            questionImageView = itemView.findViewById(R.id.QuestionImageView);
            questionActivityButton= itemView.findViewById(R.id.QuestionActivityButton);

            //Change the Activity to the Question activity with the question that is clicked
            questionActivityButton.setOnClickListener( view-> {
                Intent intent = new Intent(context, QuestionActivity.class);
                intent.putExtra("Question", clickedQuestion);

                context.startActivity(intent);
            });
        }

        //LinkData is used to make a link between the question and the button
        public void linkData(Question question) {
            clickedQuestion = question;
        }
    }
}

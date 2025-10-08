package com.example.flashcard;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Question implements Parcelable {

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

    protected Question(Parcel in) {
        id = in.readInt();
        image = in.readString();
        answers = in.createStringArrayList();
        correct = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(image);
        dest.writeStringList(answers);
        dest.writeInt(correct);
    }
}

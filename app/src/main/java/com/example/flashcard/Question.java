package com.example.flashcard;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Question implements Parcelable {
    public Question(List<String> answers, String image, int correct) {
        this.Image = image;
        this.Answers = answers;
        this.correct = correct;
    }

    public String Image;
    public List<String> Answers;
    public int correct;

    protected Question(Parcel in) {
        Image= in.readString();
        Answers = in.createStringArrayList();
        correct = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Image);
        dest.writeStringList(Answers);
        dest.writeInt(correct);
    }

    @Override
    public int describeContents() {
        return 0;
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
}

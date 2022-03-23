package com.ituto.android.Models;

import java.util.ArrayList;

public class Question {

    private String question, answer, tuteeAnswer;
    private ArrayList<String> choices;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

    public String getTuteeAnswer() {
        return tuteeAnswer;
    }

    public void setTuteeAnswer(String tuteeAnswer) {
        this.tuteeAnswer = tuteeAnswer;
    }
}

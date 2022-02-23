package com.ituto.android.Models;

import java.util.ArrayList;

public class Tutor {

    private String userID, availability;
    private ArrayList<String> subjects;
    private int numOfReviews;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    public int getNumOfReviews() {
        return numOfReviews;
    }

    public void setNumOfReviews(int numOfReviews) {
        this.numOfReviews = numOfReviews;
    }
}

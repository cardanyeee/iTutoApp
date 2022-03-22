package com.ituto.android.Models;

import java.util.ArrayList;

public class Tutor extends User {

    private String tutorID, userID;
    private ArrayList<String> subjects;
    private ArrayList<String> daysArrayList;
    private ArrayList<Time> timeArrayList;
    private int numOfReviews;

    public String getTutorID() {
        return tutorID;
    }

    public void setTutorID(String tutorID) {
        this.tutorID = tutorID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getDaysArrayList() {
        return daysArrayList;
    }

    public void setDaysArrayList(ArrayList<String> daysArrayList) {
        this.daysArrayList = daysArrayList;
    }

    public ArrayList<Time> getTimeArrayList() {
        return timeArrayList;
    }

    public void setTimeArrayList(ArrayList<Time> timeArrayList) {
        this.timeArrayList = timeArrayList;
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

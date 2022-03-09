package com.ituto.android.Models;

import java.util.ArrayList;

public class Session {

    private User tutee;
    private Tutor tutor;
    private Subject subject;
    private String sessionID, description, startDate, endDate;
    private ArrayList<String> assessments;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public User getTutee() {
        return tutee;
    }

    public void setTutee(User tutee) {
        this.tutee = tutee;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getAssessments() {
        return assessments;
    }

    public void setAssessments(ArrayList<String> assessments) {
        this.assessments = assessments;
    }
}

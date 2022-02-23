package com.ituto.android.Models;

public class Review {

    private String tutee, comment;
    private int Rating;

    public String getTutee() {
        return tutee;
    }

    public void setTutee(String tutee) {
        this.tutee = tutee;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }
}

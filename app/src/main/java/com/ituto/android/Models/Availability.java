package com.ituto.android.Models;

import java.util.ArrayList;

public class Availability {

    private ArrayList<String> days;
    private ArrayList<Time> time;

    public ArrayList<String> getDays() {
        return days;
    }

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public ArrayList<Time> getTime() {
        return time;
    }

    public void setTime(ArrayList<Time> time) {
        this.time = time;
    }
}

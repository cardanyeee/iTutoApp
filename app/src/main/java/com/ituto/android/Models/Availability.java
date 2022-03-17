package com.ituto.android.Models;

import java.util.ArrayList;

public class Availability {

    private ArrayList<String> days;
    private String morningMin, morningMax, afternoonMin, afternoonMax, eveningMin, eveningMax;

    public ArrayList<String> getDays() {
        return days;
    }

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public String getMorningMin() {
        return morningMin;
    }

    public void setMorningMin(String morningMin) {
        this.morningMin = morningMin;
    }

    public String getMorningMax() {
        return morningMax;
    }

    public void setMorningMax(String morningMax) {
        this.morningMax = morningMax;
    }

    public String getAfternoonMin() {
        return afternoonMin;
    }

    public void setAfternoonMin(String afternoonMin) {
        this.afternoonMin = afternoonMin;
    }

    public String getAfternoonMax() {
        return afternoonMax;
    }

    public void setAfternoonMax(String afternoonMax) {
        this.afternoonMax = afternoonMax;
    }

    public String getEveningMin() {
        return eveningMin;
    }

    public void setEveningMin(String eveningMin) {
        this.eveningMin = eveningMin;
    }

    public String getEveningMax() {
        return eveningMax;
    }

    public void setEveningMax(String eveningMax) {
        this.eveningMax = eveningMax;
    }
}

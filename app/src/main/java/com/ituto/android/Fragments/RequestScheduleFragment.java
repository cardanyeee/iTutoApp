package com.ituto.android.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.applandeo.materialcalendarview.CalendarView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.ituto.android.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RequestScheduleFragment extends Fragment {

    private View view;
    private CalendarView calendarTutorSchedule;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_schedule, container, false);
        init();
        return view;
    }

    private void init() {

        calendarTutorSchedule = view.findViewById(R.id.calendarTutorSchedule);
        calendarTutorSchedule.setMinimumDate(Calendar.getInstance());
    }

    private void setSchedule() {

    }
}
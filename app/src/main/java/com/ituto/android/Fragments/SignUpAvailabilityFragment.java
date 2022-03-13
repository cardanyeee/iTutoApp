package com.ituto.android.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ituto.android.Constant;
import com.ituto.android.R;
import com.ituto.android.TutorSubjectsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpAvailabilityFragment extends Fragment {
    
    public static View view;

    private Button btnSignUpTutor;
    private ProgressDialog dialog;
    private TextView txtMinTime, txtMinTime2, txtMaxTime, txtMaxTime2;
    public static TextView time;
    public static TextView period;
    public static ArrayList<String> days;
    private SharedPreferences sharedPreferences;
    private Calendar calendar;
    public static TimePickerDialog.OnTimeSetListener onTimeSetListener;
    public static int hour;
    public static int minute;
    private String availability;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up_availability, container, false);
        init();
        return view;
    }

    private void init() {
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        txtMinTime = view.findViewById(R.id.txtMinTime);
        txtMinTime2 = view.findViewById(R.id.txtMinTime2);
        txtMaxTime = view.findViewById(R.id.txtMaxTime);
        txtMaxTime2 = view.findViewById(R.id.txtMaxTime2);

        btnSignUpTutor = view.findViewById(R.id.btnSignUpTutor);

        days = new ArrayList<String>();

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                hour = hourOfDay;
                minute = minuteOfHour;
                calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, hour, minute);
                time.setText(DateFormat.format("hh:mm", calendar));
                period.setText(DateFormat.format("aa", calendar));
            }
        };

        btnSignUpTutor.setOnClickListener(v -> {
            JSONArray availabilityArray = new JSONArray();
            for ( int a = 0; a < days.size(); a++) {
                JSONObject availabilityObject = new JSONObject();
                try {
                    availabilityObject.put("day", days.get(a));
                    availabilityObject.put("startTime", txtMinTime.getText().toString() + txtMinTime2.getText().toString());
                    availabilityObject.put("endTime", txtMaxTime.getText().toString() + txtMaxTime2.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                availabilityArray.put(availabilityObject);
            }
            Bundle args = getArguments();
            SignUpSubjectsFragment signUpSubjectsFragment = new SignUpSubjectsFragment();
            args.putString("availability", availabilityArray.toString());
            signUpSubjectsFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.frameAuthContainer, signUpSubjectsFragment).commit();
        });
    }

    private void convertArrayToString() {
        Object[] daysArray = days.toArray();
        for(int i = 0; i < daysArray.length ; i++){
            availability += (String)daysArray[i] + " ";
        }
    }

}
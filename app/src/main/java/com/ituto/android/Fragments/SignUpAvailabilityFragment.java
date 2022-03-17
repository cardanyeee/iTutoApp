package com.ituto.android.Fragments;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.R;
import com.ituto.android.Utils.RangeTimePickerDialog;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SignUpAvailabilityFragment extends Fragment {
    
    public static View view;

    private Button btnSignUpTutor;
    private ProgressDialog dialog;
    private TextView txtStartMorningTime, txtEndMorningTime, txtStartAfternoonTime, txtEndAfternoonTime, txtStartEveningTime, txtEndEveningTime;
    public static TextInputEditText maxTime, minTime;
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

        txtStartMorningTime = view.findViewById(R.id.txtStartMorningTime);
        txtEndMorningTime = view.findViewById(R.id.txtEndMorningTime);
        txtStartAfternoonTime = view.findViewById(R.id.txtStartAfternoonTime);
        txtEndAfternoonTime = view.findViewById(R.id.txtEndAfternoonTime);
        txtStartEveningTime = view.findViewById(R.id.txtStartEveningTime);
        txtEndEveningTime = view.findViewById(R.id.txtEndEveningTime);
        btnSignUpTutor = view.findViewById(R.id.btnSignUpTutor);
        days = new ArrayList<String>();

        txtStartMorningTime.setOnClickListener(view -> {
            RangeTimePickerDialog rangeTimePickerDialog = new RangeTimePickerDialog(getContext(), onTimeSetListener, 6, 0, false);
            rangeTimePickerDialog.setMin(6, 0);
            rangeTimePickerDialog.setMax(12, 0);
            rangeTimePickerDialog.show();
        });

        txtEndMorningTime.setOnClickListener(view -> {
            if (txtStartMorningTime.getText().toString().isEmpty()) {
                StyleableToast.makeText(getContext(), "Set start time first", R.style.CustomToast).show();
                return;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
            try {
                Date date = dateFormat.parse(txtStartMorningTime.getText().toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                RangeTimePickerDialog rangeTimePickerDialog = new RangeTimePickerDialog(getContext(), onTimeSetListener, 12, 0, false);
                rangeTimePickerDialog.setMin(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                rangeTimePickerDialog.setMax(12, 0);
                rangeTimePickerDialog.show();
            } catch (ParseException e) {
            }
        });

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                hour = hourOfDay;
                minute = minuteOfHour;
                calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, hour, minute);
                txtStartMorningTime.setText(DateFormat.format("hh:mm aa", calendar));
            }
        };

        btnSignUpTutor.setOnClickListener(v -> {
            JSONArray availabilityArray = new JSONArray();
            for ( int a = 0; a < days.size(); a++) {
                JSONObject availabilityObject = new JSONObject();
                try {
                    availabilityObject.put("day", days.get(a));
//                    availabilityObject.put("startTime", txtMinTime.getText().toString() + txtMinTime2.getText().toString());
//                    availabilityObject.put("endTime", txtMaxTime.getText().toString() + txtMaxTime2.getText().toString());
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
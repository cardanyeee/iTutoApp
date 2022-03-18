package com.ituto.android.Fragments.AuthFragments;

import android.app.ProgressDialog;
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

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

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
    private MaterialCheckBox chkMorning, chkAfternoon, chkEvening;
    private TextView txtStartMorningTime, txtEndMorningTime, txtStartAfternoonTime, txtEndAfternoonTime, txtStartEveningTime, txtEndEveningTime;
    public TextInputEditText time;
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

        chkMorning = view.findViewById(R.id.chkMorning);
        chkAfternoon = view.findViewById(R.id.chkAfternoon);
        chkEvening = view.findViewById(R.id.chkEvening);

        txtStartMorningTime = view.findViewById(R.id.txtStartMorningTime);
        txtEndMorningTime = view.findViewById(R.id.txtEndMorningTime);
        txtStartAfternoonTime = view.findViewById(R.id.txtStartAfternoonTime);
        txtEndAfternoonTime = view.findViewById(R.id.txtEndAfternoonTime);
        txtStartEveningTime = view.findViewById(R.id.txtStartEveningTime);
        txtEndEveningTime = view.findViewById(R.id.txtEndEveningTime);
        btnSignUpTutor = view.findViewById(R.id.btnSignUpTutor);
        days = new ArrayList<String>();

        txtStartMorningTime.setOnClickListener(view -> {
            if (chkMorning.isChecked()) {
                time = (TextInputEditText) txtStartMorningTime;
                TimePickerDialog timePickerDialog;
                timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 6, 0, false);
                timePickerDialog.setMinTime(6, 0, 0);
                timePickerDialog.setMaxTime(11, 59, 0);
                timePickerDialog.setTimeInterval(1, 5, 60);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "StartMorningTime");
            }
        });

        txtEndMorningTime.setOnClickListener(view -> {
            if (chkMorning.isChecked()) {
                time = (TextInputEditText) txtEndMorningTime;
                if (txtStartMorningTime.getText().toString().isEmpty()) {
                    StyleableToast.makeText(getContext(), "Set minimum time first", R.style.CustomToast).show();
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                try {
                    Date date = dateFormat.parse(txtStartMorningTime.getText().toString());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    TimePickerDialog timePickerDialog;
                    Log.d("terteert", String.valueOf(cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE)));
                    timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 11, 55, false);
                    timePickerDialog.setMinTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
                    timePickerDialog.setMaxTime(11, 55, 0);
                    timePickerDialog.setTimeInterval(1, 5, 60);
                    timePickerDialog.show(getActivity().getSupportFragmentManager(), "EndMorningTime");
                } catch (ParseException e) {

                }
            }
        });

        txtStartAfternoonTime.setOnClickListener(view -> {
            if (chkAfternoon.isChecked()) {
                time = (TextInputEditText) txtStartAfternoonTime;
                TimePickerDialog timePickerDialog;
                timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 12, 0, false);
                timePickerDialog.setMinTime(12, 0, 0);
                timePickerDialog.setMaxTime(17, 0, 0);
                timePickerDialog.setTimeInterval(1, 5, 60);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "StartAfternoonTime");
            }
        });

        txtEndAfternoonTime.setOnClickListener(view -> {
            if (chkAfternoon.isChecked()) {
                time = (TextInputEditText) txtEndAfternoonTime;
                if (txtStartAfternoonTime.getText().toString().isEmpty()) {
                    StyleableToast.makeText(getContext(), "Set minimum time first", R.style.CustomToast).show();
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                try {
                    Date date = dateFormat.parse(txtStartAfternoonTime.getText().toString());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    TimePickerDialog timePickerDialog;
                    timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 17, 0, false);
                    timePickerDialog.setMinTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
                    timePickerDialog.setMaxTime(17, 0, 0);
                    timePickerDialog.setTimeInterval(1, 5, 60);
                    timePickerDialog.show(getActivity().getSupportFragmentManager(), "EndAfternoonTime");
                } catch (ParseException e) {
                }
            }
        });

        txtStartEveningTime.setOnClickListener(view -> {
            if (chkEvening.isChecked()) {
                time = (TextInputEditText) txtStartEveningTime;
                TimePickerDialog timePickerDialog;
                timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 17, 0, false);
                timePickerDialog.setMinTime(17, 0, 0);
                timePickerDialog.setMaxTime(23, 59, 0);
                timePickerDialog.setTimeInterval(1, 5, 60);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "StartAfternoonTime");
            }
        });

        txtEndEveningTime.setOnClickListener(view -> {
            if (chkEvening.isChecked()) {
                time = (TextInputEditText) txtEndEveningTime;
                if (txtStartEveningTime.getText().toString().isEmpty()) {
                    StyleableToast.makeText(getContext(), "Set minimum time first", R.style.CustomToast).show();
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                try {
                    Date date = dateFormat.parse(txtStartEveningTime.getText().toString());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    TimePickerDialog timePickerDialog;
                    timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 23, 59, false);
                    timePickerDialog.setMinTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
                    timePickerDialog.setMaxTime(23, 59, 0);
                    timePickerDialog.setTimeInterval(1, 5, 60);
                    timePickerDialog.show(getActivity().getSupportFragmentManager(), "EndAfternoonTime");
                } catch (ParseException e) {
                }
            }
        });

        onTimeSetListener = (view, hourOfDay, minute, second) -> {
            calendar = Calendar.getInstance();
            calendar.set(0, 0, 0, hourOfDay, minute);
            time.setText(DateFormat.format("hh:mm aa", calendar));
        };

        btnSignUpTutor.setOnClickListener(v -> {
            if (!validate()) {
                return;
            }

            JSONObject availabilityObject = new JSONObject();
            JSONArray daysJSONArray = new JSONArray();
            JSONArray timeJSONArray = new JSONArray();
            for (int a = 0; a < days.size(); a++) {
                daysJSONArray.put(days.get(a));
            }
            try {
                availabilityObject.put("days", daysJSONArray);

                if (chkMorning.isChecked()) {
                    JSONObject morning = new JSONObject();
                    morning.put("timeOfDay", "Morning");
                    morning.put("min", txtStartMorningTime.getText().toString().trim());
                    morning.put("max", txtEndMorningTime.getText().toString().trim());
                    timeJSONArray.put(morning);
                }

                if (chkAfternoon.isChecked()) {
                    JSONObject afternoon = new JSONObject();
                    afternoon.put("timeOfDay", "Afternoon");
                    afternoon.put("min", txtStartAfternoonTime.getText().toString().trim());
                    afternoon.put("max", txtEndAfternoonTime.getText().toString().trim());
                    timeJSONArray.put(afternoon);
                }

                if (chkEvening.isChecked()) {
                    JSONObject evening = new JSONObject();
                    evening.put("timeOfDay", "Evening");
                    evening.put("min", txtStartEveningTime.getText().toString().trim());
                    evening.put("max", txtEndEveningTime.getText().toString().trim());
                    timeJSONArray.put(evening);
                }

                availabilityObject.put("time", timeJSONArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Bundle args = getArguments();
            SignUpSubjectsFragment signUpSubjectsFragment = new SignUpSubjectsFragment();
            args.putString("availability", availabilityObject.toString());
            signUpSubjectsFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.frameAuthContainer, signUpSubjectsFragment).commit();
        });
    }

    private boolean validate() {

        if (!(days.size() > 0)) {
            StyleableToast.makeText(getContext(), "Please select days on when you are available", R.style.CustomToast).show();
            return false;
        }

        if (chkMorning.isChecked() && (txtStartMorningTime.getText().toString().trim().isEmpty() || txtEndMorningTime.getText().toString().trim().isEmpty())) {
            StyleableToast.makeText(getContext(), "Please complete your morning schedule for availability", R.style.CustomToast).show();
            return false;
        }

        if (chkAfternoon.isChecked() && (txtStartAfternoonTime.getText().toString().trim().isEmpty() || txtEndAfternoonTime.getText().toString().trim().isEmpty())) {
            StyleableToast.makeText(getContext(), "Please complete your afternoon schedule  for availability", R.style.CustomToast).show();
            return false;
        }

        if (chkEvening.isChecked() && (txtStartEveningTime.getText().toString().trim().isEmpty() || txtEndEveningTime.getText().toString().trim().isEmpty())) {
            StyleableToast.makeText(getContext(), "Please complete your evening schedule  for availability", R.style.CustomToast).show();
            return false;
        }

        return true;
    }

}
package com.ituto.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.CalendarView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ituto.android.Constant;
import com.ituto.android.Fragments.MainFragments.HomeFragment;
import com.ituto.android.Models.Availability;
import com.ituto.android.Models.Subject;
import com.ituto.android.Models.Tutor;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestScheduleFragment extends Fragment {

    private View view;

    private CircleImageView imgTutorProfile;
    private TextView txtName, txtCourse;
    private TextInputLayout txtLayoutSubject, txtLayoutDescription;
    private AutoCompleteTextView txtSubject;
    private TextInputEditText txtDescription;
    private CalendarView calendarTutorSchedule;
    private Chip chpMorning, chpAfternoon, chpEvening;
    private Button btnConfirmSchedule;

    private Dialog dialog;
    private String tutorID;
    private String subjectID;
    private String startDate;

    private ArrayList<Subject> subjectArrayList;
    private ArrayList<String> stringSubjectArrayList;
    private ArrayList<Integer> availableDays;
    private SharedPreferences sharedPreferences;

    private Tutor tutor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_schedule, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        imgTutorProfile = view.findViewById(R.id.imgTutorProfile);
        txtName = view.findViewById(R.id.txtName);

        txtLayoutSubject = view.findViewById(R.id.txtLayoutSubject);
        txtLayoutDescription = view.findViewById(R.id.txtLayoutDescription);

        txtCourse = view.findViewById(R.id.txtCourse);

        txtSubject = view.findViewById(R.id.txtSubject);
        txtDescription = view.findViewById(R.id.txtDescription);

        calendarTutorSchedule = view.findViewById(R.id.calendarTutorSchedule);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DATE, -1);
        calendarTutorSchedule.setMinimumDate(minDate);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 1);
        calendarTutorSchedule.setMaximumDate(maxDate);
        btnConfirmSchedule = view.findViewById(R.id.btnConfirmSchedule);

        chpMorning = view.findViewById(R.id.chpMorning);
        chpAfternoon = view.findViewById(R.id.chpAfternoon);
        chpEvening = view.findViewById(R.id.chpEvening);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        tutorID = getArguments().getString("_id");

        getTutorProfile();

        btnConfirmSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    confirmSchedule();
                }
            }
        });

        txtSubject.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            subjectID = subjectArrayList.get(stringSubjectArrayList.indexOf(selected)).getId();
        });

        calendarTutorSchedule.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                startDate = format.format(clickedDayCalendar.getTime());
            }
        });

    }

    private void setDisableDaysInCalendar(ArrayList<Integer> availableDays) {

        Calendar date;
        List<Calendar> disabledDays = new ArrayList<>();
        int weeks = 52;

        for (int a = 1; a < 8; a++) {
            if (!availableDays.contains(a)) {
                for (int i = -7; i < (weeks * 7); i = i + 7) {
                    date = Calendar.getInstance();
                    date.add(Calendar.DAY_OF_YEAR, (a - date.get(Calendar.DAY_OF_WEEK) + 7 + i));
                    // saturday = Calendar.getInstance();
                    // saturday.add(Calendar.DAY_OF_YEAR, (Calendar.SATURDAY - saturday.get(Calendar.DAY_OF_WEEK) + i));
                    // weekends.add(saturday);
                    disabledDays.add(date);
                }
            }

        }
        calendarTutorSchedule.setDisabledDays(disabledDays);
    }

    private void getTutorProfile() {
        stringSubjectArrayList = new ArrayList<>();
        subjectArrayList = new ArrayList<>();
        availableDays = new ArrayList<>();
        tutor = new Tutor();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.TUTOR_PROFILE + "/" + tutorID, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONObject tutorObject = object.getJSONObject("tutor");
                    JSONObject userObject = tutorObject.getJSONObject("userID");
                    JSONObject avatarObject = userObject.getJSONObject("avatar");
                    JSONObject courseObject = userObject.getJSONObject("course");
                    JSONArray subjectsJSONArray = tutorObject.getJSONArray("subjects");
                    JSONObject availabilityObject = tutorObject.getJSONObject("availability");
                    JSONArray daysArray = availabilityObject.getJSONArray("days");
                    JSONArray timeJSONArray = availabilityObject.getJSONArray("time");

                    tutor.setUserID(userObject.getString("_id"));

                    for (int i = 0; i < subjectsJSONArray.length(); i++) {
                        JSONObject subjectObject = subjectsJSONArray.getJSONObject(i);

                        if (subjectObject.getBoolean("active")) {
                            Subject subject = new Subject();

                            subject.setId(subjectObject.getString("_id"));
                            subject.setCode(subjectObject.getString("code"));
                            subject.setName(subjectObject.getString("name"));

                            stringSubjectArrayList.add(subjectObject.getString("name"));
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                    getContext(),
                                    R.layout.item_dropdown,
                                    R.id.txtDropdownItem,
                                    stringSubjectArrayList
                            );
                            subjectArrayList.add(subject);
                            txtSubject.setAdapter(arrayAdapter);
                        }

                    }


                    for (int i = 0; i < daysArray.length(); i++) {
                        availableDays.add(parseDayString(daysArray.getString(i)));
                    }

                    if (timeJSONArray.length() > 0) {
                        for (int a = 0; a < timeJSONArray.length(); a++) {
                            JSONObject time = timeJSONArray.getJSONObject(a);
                            if (time.get("timeOfDay").equals("Morning")) {
                                chpMorning.setVisibility(View.VISIBLE);
                                chpMorning.setText(time.getString("min") + " - " + time.getString("max"));
                            }

                            if (time.get("timeOfDay").equals("Afternoon")) {
                                chpAfternoon.setVisibility(View.VISIBLE);
                                chpAfternoon.setText(time.getString("min") + " - " + time.getString("max"));
                            }

                            if (time.get("timeOfDay").equals("Evening")) {
                                chpEvening.setVisibility(View.VISIBLE);
                                chpEvening.setText(time.getString("min") + " - " + time.getString("max"));
                            }
                        }
                    }

                    Picasso.get().load(avatarObject.getString("url")).resize(500, 0).into(imgTutorProfile);
                    txtName.setText(userObject.getString("firstname") + " " + userObject.getString("lastname"));
                    txtCourse.setText(courseObject.getString("name"));

                    setDisableDaysInCalendar(availableDays);

                }

                dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                btnConfirmSchedule.setEnabled(false);
                dialog.dismiss();
            }

        }, error -> {
            dialog.dismiss();
            btnConfirmSchedule.setEnabled(false);
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }

    private void confirmSchedule() {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REQUEST_SESSION, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    ).replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            StyleableToast.makeText(getContext(), "Unable to request schedule", R.style.CustomToast).show();
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("tutor", tutor.getUserID());
                map.put("subject", subjectID);
                map.put("description", txtDescription.getText().toString().trim());
                map.put("startDate", startDate);
                map.put("time", checkWhatTime().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private boolean validate() {

        if (txtSubject.getText().toString().isEmpty()) {
            txtLayoutSubject.setErrorEnabled(true);
            txtLayoutSubject.setError("Please select a subject");
            return false;
        }

        if (txtDescription.getText().toString().isEmpty()) {
            txtLayoutDescription.setErrorEnabled(true);
            txtLayoutDescription.setError("Please input on which area of the subject you are having trouble with");
            return false;
        }

        if (startDate == null) {
            StyleableToast.makeText(getContext(), "Select a date on when would you like to start", R.style.CustomToast).show();
        }

        if (!(chpMorning.isChecked() || chpAfternoon.isChecked() || chpEvening.isChecked())) {
            StyleableToast.makeText(getContext(), "Select which preferable time to start sessions", R.style.CustomToast).show();
        }

        return true;
    }

    private JSONObject checkWhatTime() {
        JSONObject timeJSONObject = new JSONObject();
        try {
            if (chpMorning.isChecked()) {
                timeJSONObject.put("timeOfDay", "Morning");
                timeJSONObject.put("min", chpMorning.getText().toString().split(" - ")[0].trim());
                timeJSONObject.put("max", chpMorning.getText().toString().split(" - ")[1].trim());
            }

            if (chpAfternoon.isChecked()) {
                timeJSONObject.put("timeOfDay", "Afternoon");
                timeJSONObject.put("min", chpAfternoon.getText().toString().split(" - ")[0].trim());
                timeJSONObject.put("max", chpAfternoon.getText().toString().split(" - ")[1].trim());
            }

            if (chpEvening.isChecked()) {
                timeJSONObject.put("timeOfDay", "Evening");
                timeJSONObject.put("min", chpEvening.getText().toString().split(" - ")[0].trim());
                timeJSONObject.put("max", chpEvening.getText().toString().split(" - ")[1].trim());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAGTAGTAGTAG", timeJSONObject.toString());
        return timeJSONObject;
    }

    private int parseDayString(String day) {
        switch (day) {
            case "Sunday":
                return Calendar.SUNDAY;
            case "Monday":
                return Calendar.MONDAY;
            case "Tuesday":
                return Calendar.TUESDAY;
            case "Wednesday":
                return Calendar.WEDNESDAY;
            case "Thursday":
                return Calendar.THURSDAY;
            case "Friday":
                return Calendar.FRIDAY;
            case "Saturday":
                return Calendar.SATURDAY;
        }
        return 0;
    }
}